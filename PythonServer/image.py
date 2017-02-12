# -*- coding:utf-8 -*-
import os
import threading
import Queue
import thread
from multiprocessing.pool import ThreadPool
import time

from flask import Flask, request, send_file, url_for, redirect
from werkzeug.utils import secure_filename

import socket
import tstruct
import fcntl

epollreactor.install()
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sockfd = sock.fileno()
SIOCGIFADDR = 0x8915


def get_ip(iface='eth0'):
    ifreq = struct.pack('16sH14s', iface, socket.AF_INET, '\x00' * 14)
    try:
        res = fcntl.ioctl(sockfd, SIOCGIFADDR, ifreq)
    except:
        return None
    ip = struct.unpack('16sH2x4s8x', res)[2]
    return socket.inet_ntoa(ip)


class Catcher(threading.Thread):  # Thread Extends
    def __init__(self, id, dataQueue):
        threading.Thread.__init__(self)
        self.id = id  # for print
        self.dataQueue = dataQueue  # data queue

    def run(self):
        while True:  # thread task
            data = self.dataQueue.get()  # get file data
            if data and allowed_file(data.filename):
                filename = secure_filename(data.filename)  # CSRF 검사
                data.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
                url_file_path = "http://" + get_ip('eth0') + ":3331/" + os.getcwd() + '/image/' + filename
                print url_file_path
                return url_file_path


app = Flask(__name__)

StringQueue = Queue.Queue()

app.config['UPLOAD_FOLDER'] = './image'
app.config['UPLOAD_THUMBNAIL_FOLDER'] = './thumb'
app.config['DEFAULT_PORT'] = 3331

ALLOWED_EXTENSIONS = set(['png', 'jpg', 'jpeg', 'gif'])


def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS


@app.route("/")
@app.route("/image/<fileName>", methods=["GET", "POST"])
def load_image_file(file_name):
    return send_file(app.config['UPLOAD_FOLDER'] + '/' + file_name, mimetype='image')


@app.route("/thumb/<fileName>", methods=["GET", "POST"])
def load_thumbnail_image_file(file_name):
    return send_file(app.config['UPLOAD_THUMBNAIL_FOLDER'] + '/' + file_name, mimetype='image')


@app.route("/upload", methods=['GET', 'POST'])
def upload_image_file():
    if request.method == 'POST':  # Request Post 이고
        if 'file' not in request.files:  # 실제로 파일 파라미터가 포함 되어 있는지 아닌지 검사
            return "File Upload Error"

        upload_file = request.files['file']  # file 객체를 가져옴
        StringQueue.put(upload_file)
        upload_thread = Catcher("file_upload", StringQueue)
        upload_thread.setDaemon(True)
        upload_thread.start()
        upload_thread.dataQueue.join()
        result = StringQueue.get()


        print result
        return result

        # if file and allowed_file(file.filename): # Image 파일 인지 검사
        #    filename = secure_filename(file.filename) # CSRF 검사

        # todo not folder processing
        # file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename)) # 파일 저장
        # return app.config['UPLOAD_FOLDER'] + '/' + filename
        # return redirect(url_for('load_image_file', filename=filename)) # 파일의 url 반환.

    return 'IMAGE_UPLOAD_FAIL'


@app.route("/upload_thumbnail", methods=['GET', 'POST'])
def upload_thumbnail_image_file():
    if request.method == 'POST':
        if 'file' not in request.files:
            print 'Thumbnail UPLOAD Error'
        file = request.files['file']
        if file and allowed_file(file.filename):
            filename = secure_filename(file.filename)
            file.save(os.path.join(app.config['UPLOAD_THUMBNAIL_FOLDER'], filename))
            return redirect(url_for('load_image_file', filename=filename))

    return 'THUMB_UPLOAD_FAIL'


if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=app.config['DEFAULT_PORT'])
