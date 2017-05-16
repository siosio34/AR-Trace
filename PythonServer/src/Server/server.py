# -*- coding: utf-8 -*-
from flask import Flask, request
from tasks.celery_handler import celery_review_delete, celery_review_get, celery_review_insert
from tasks.celery_handler import celery_review_like_update, celery_review_count
from tasks.celery_handler import celery_user_dropout, celery_user_login, celery_user_logout

flask_app = Flask(__name__)
import sys
print sys.path

@flask_app.route('/reviews', methods=['GET', 'POST', 'DELETE'])
def review_request():
    if request.method == 'POST':
        celery_review_insert.delay(request.get_json())
        return 'review write ok'
    elif request.method == 'GET':
        if request.args.get("placeName"):
            raw_data = celery_review_get.delay(request.args.get("placeName"))
            result = raw_data.get()
        elif request.args.get("traceID"):
            raw_data = celery_review_like_update.delay(request.args.get("check"), request.args.get("traceID"))
            result = raw_data.get()
        return str(result)
    elif request.method == 'DELETE':
        celery_review_delete.delay(request.args.get('traceID'))
        return 'ok'


@flask_app.route('/reviews/count', methods=['GET'])
def review_count_request():
    raw_data = celery_review_count.delay(request.args.get('placeName'))
    result = raw_data.get()
    return str(result)


@flask_app.route('/users/login', methods=['POST'])
def login():
    result = celery_user_login(request.get_json())
    return str(result)


@flask_app.route('/users/logout', methods=['GET'])
def logout():
    celery_user_logout.delay(request.args.get('userId'))
    return 'ok'


@flask_app.route('/users/dropout', methods=['DELETE'])
def dropout():
    celery_user_dropout.delay(request.args.get('userId'))
    return 'ok'


if __name__ == '__main__':
    flask_app.run(host="0.0.0.0", port=3332)

