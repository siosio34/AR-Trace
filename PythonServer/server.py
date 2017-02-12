import datetime
import json
import redis
from flask import Flask, request
from itsdangerous import URLSafeTimedSerializer
from database import engine_user
from data_celery import insert_review, delete_review, update_review, get_review_p, get_review_l

flask_app = Flask(__name__)

ID_NOT_EXIST = 'id not exist'
DATA_NOT_EXIST = 'some data not exist'
WRONG_CONNECTION = 'wrong connection'
USER_REGISTER = 'user register'
USER_DROPOUT = 'user dropout'
SESSION_MADE = 'session made'
SESSION_EXIST = 'session exist'
SESSION_NOT_EXIST = 'session not exist'
SESSION_DISCONNECTION = 'session disconnection'
TOKEN_KEY = 'a_temp_random_key'
LOGIN_GET = 'login get'
LOGIN_POST = 'login post'
LOGOUT_GET = 'logout get'
DROPOUT_GET = 'dropout get'
LOGOUT = 'logout'
DROPOUT = 'dropout'
LEAST_NEED_ARGUMENT_NUMBER = 4
TTL = 2592000

flask_app.debug = True
flask_app.secret_key = TOKEN_KEY

login_serializer = URLSafeTimedSerializer(flask_app.secret_key)
db = redis.Redis('192.168.1.207', port=6379)
logger = open('/var/log/login.log', 'a')


@flask_app.route('/review', methods=['post', 'get'])
def review_request():
    if request.method == 'POST':
        r_json = request.get_json()
        insert_review.delay(r_json)
        return 'review write ok'

    elif request.method == 'GET':
        if request.args.get("lat"):
            lat = request.args.get("lat")
            lon = request.args.get("lon")
            temp = get_review_l.delay(lat, lon)
            result = temp.get()
        else:
            placeName = request.args.get("placeName")
            temp = get_review_p.delay(placeName)
            result = temp.get()
        return str(result)


@flask_app.route('/delete', methods=['GET'])
def review_delete():
    if request.method == 'GET':
        traceID = request.args.get('traceID')
        delete_review.delay(traceID)
        print traceID
    return 'delete ok'


@flask_app.route('/update', methods=['GET'])
def review_update():
    traceID = request.args.get("traceID")
    temp = update_review.delay(traceID)
    result = temp.get()
    return str(result)


# @flask_app.route('/edit')
# def review_edit():
#     return 'edit ok'


def get_auth_token(user_id):
    data = [str(user_id)]
    return login_serializer.dumps(data)


def write_log(stat, msg, data):
    global logger
    if stat is LOGIN_GET:
        logs = "{} {} id={}\n".format(stat, msg, data["id"])
        logger.write(logs)
    elif stat is LOGIN_POST:
        if msg == USER_REGISTER:
            logs = "{} {} id={} name={} email={} image_url={}\n".format(stat, msg, data["id"], data["name"],
                                                                        data["email"], data["image_url"])
            logger.write(logs)
        elif msg == SESSION_MADE:
            logs = "{} {} id={} token={}\n".format(stat, msg, data["id"], data["token"])
            logger.write(logs)
        else:
            logs = "{} {}\n".format(stat, msg)
            logger.write(logs)
    elif stat is LOGOUT_GET:
        if data is not None:
            logs = "{} {} id={}\n".format(stat, msg, data["id"])
            logger.write(logs)
        else:
            logs = "{} {}\n".format(stat, msg)
            logger.write(logs)
    elif stat is DROPOUT_GET:
        if data is not None:
            logs = "{} {} id={}\n".format(stat, msg, data["id"])
            logger.write(logs)
        else:
            logs = "{} {}\n".format(stat, msg)
            logger.write(logs)
    logger.flush()
    return True

@flask_app.route('/', methods=['GET', 'POST'])
@flask_app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'GET':
        if db.get(request.args.get('id')) is not None:
            db.expire(request.args.get('id'), TTL)  # update ttl everytime of access
            jsn = {"id": request.args.get('id')}
            write_log(LOGIN_GET, SESSION_EXIST, jsn)
            return SESSION_EXIST, 200
        return SESSION_NOT_EXIST, 400
    elif request.method == 'POST':
        event = request.get_json()
        if len(event) < LEAST_NEED_ARGUMENT_NUMBER:
            write_log(LOGIN_POST, DATA_NOT_EXIST, None)
            return DATA_NOT_EXIST, 400
        conn = engine_user.connect()
        q = "SELECT * FROM users WHERE id = '{}' ;".format(event['id'])
        query_result = conn.execute(q)
        if query_result is None:
            i = "INSERT INTO users VALUES('{}', '{}', '{}', '{}');".format(event['id'], event['name'], event['email'],
                                                                           event['image_url'])
            conn.execute(i)
            write_log(LOGIN_POST, USER_REGISTER, event)
        event['token'] = get_auth_token(event['id'])
        db.set(event['id'], event['token'])
        db.expire(event['id'], TTL)
        write_log(LOGIN_POST, SESSION_MADE, event)
        conn.close()
        return json.dumps(event), 200
    return WRONG_CONNECTION, 400


@flask_app.route('/logout', methods=['GET'])
def logout():
    if request.method == 'GET':
        if db.get(request.args.get('id')) is not None:
            db.delete(request.args.get('id'))
            jsn = {"id": request.args.get('id')}
            write_log(LOGOUT_GET, SESSION_DISCONNECTION, jsn)
            return SESSION_DISCONNECTION, 200
        write_log(LOGOUT_GET, SESSION_NOT_EXIST, None)
        return SESSION_NOT_EXIST, 400
    return WRONG_CONNECTION, 400


@flask_app.route('/dropout', methods=['GET'])
def dropout():
    if request.method == 'GET':
        user_id = request.args.get('id')
        if user_id is None:
            write_log(DROPOUT_GET, ID_NOT_EXIST, None)
            return ID_NOT_EXIST, 400
        conn = engine_user.connect()
        q = "SELECT * FROM users WHERE id = '{}' ;".format(user_id)
        query_result = conn.execute(q)
        if query_result:
            d = "DELETE FROM users WHERE id = '{}';".format(user_id)
            conn.execute(d)
            jsn = {"id": request.args.get('id')}
            write_log(DROPOUT_GET, USER_DROPOUT, jsn)
            conn.close()
            return USER_DROPOUT, 200
        else:
            write_log(DROPOUT_GET, ID_NOT_EXIST, None)
            conn.close()
            return ID_NOT_EXIST, 400
    return WRONG_CONNECTION, 400


if __name__ == '__main__':
    flask_app.run(host="0.0.0.0", port=3331)
