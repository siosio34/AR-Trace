# -*-coding:utf-8
import base64
from flask import Flask, request, send_file, url_for, redirect
from werkzeug.utils import secure_filename
from image_celery_tasks import celery_send_image, celery_store_image, celery_send_thumbnail, celery_store_thumbnail
import socket
import os


def get_ip_address():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.connect(("8.8.8.8", 80))
    return s.getsockname()[0]


flask_app = Flask(__name__)

flask_app.config['IMAGE_FOLDER'] = 'image'
flask_app.config['THUMBNAIL_FOLDER'] = 'thumb'


@flask_app.route("/image/<filename>", methods=["GET", "POST"])
def download_image_file(filename):
    result = celery_send_image.delay(filename)
    if result.get() == 'good':
        return send_file(flask_app.config['IMAGE_FOLDER'] + '/' + filename,  mimetype='image')
    return 'IMAGE_LOAD_FAIL'


@flask_app.route("/thumb/<fileName>", methods=["GET", "POST"])
def download_thumbnail_image_file(filename):
    result = celery_store_thumbnail.delay(filename)
    if result.get():
        return send_file(flask_app.config['THUMBNAIL_FOLDER'] + '/' + filename, mimetype='image')
    return 'IMAGE_LOAD_FAIL'


@flask_app.route("/upload", methods=['GET', 'POST'])
def upload_image_file():
    if request.method == 'POST': # Request Post 이고
        if 'file' not in request.files: # 실제로 파일 파라미터가 포함 되어 있는지 아닌지 검사
            print "File Upload Error"
            return 'IMAGE_UPLOAD_FAIL'
        file = request.files['file'] # file 객체를 가져옴
        if file:# Image 파일 인지 검사
            filename = secure_filename(file.filename) # CSRF 검사
            str = base64.b64encode(file.read())
            result = celery_store_image.delay(filename, str)
            if result.get():
                print 'step02'
                url_file_path = "http://" + get_ip_address() + ":3331" + os.getcwd() + "/image/" + filename
                return url_file_path  # 파일의 url 반환.
        print 'step03'
    return 'IMAGE_UPLOAD_FAIL'


@flask_app.route("/upload_thumbnail", methods=['GET', 'POST'])
def upload_thumbnail_image_file():
    if request.method == 'POST':
        if 'file' not in request.files:
            print 'Thumbnail Upload Error'
        file = request.files['file']
        if file:
            filename = secure_filename(file.filename)  # CSRF 검사
            str = base64.b64encode(file.read())
            result = celery_store_image.delay(filename, str)
            if result.get():
                url_file_path = "http://" + get_ip_address() + ":3331" + os.getcwd() + "/image/" + filename
                return url_file_path  # 파일의 url 반환.
    return 'THUMB_UPLOAD_FAIL'


if __name__ == "__main__":
    flask_app.run(debug=True, host="0.0.0.0", port=3331)