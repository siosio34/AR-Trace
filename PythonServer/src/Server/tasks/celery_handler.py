# -*- coding: utf-8 -*-
import os
import sys
sys.path.append(os.path.dirname(os.path.abspath(os.path.dirname(__file__))))

from common.mysql_handler import *
from common.redis_handler import *
from common.const import *
from init import log_handler
from utils.make_token import get_auth_token
from celery_module import celery_app


def celery_review_response(result):
    review_json = list()
    for item in result:
        review_json.append(review.to_json(item))
    review_response = {"data": review_json}
    return review_response


def celery_users_response(result):
    user_json = list()
    for item in result:
        user_json.append(user.to_json(item))
    user_response = {"data": user_json}
    return user_response


def celery_review_single_response(result):
    for item in result:
	temp = item[0]
    count_result = str(int(temp))	
    review_single_response = {"data": count_result}

    return review_single_response


@celery_app.task(name='celery_review_count')
def celery_review_count(value):
    raw_data = mysql_count(review.table, 'placeName', value)
    result = celery_review_single_response(raw_data)
    return result


@celery_app.task(name='celery_handler.celery_review_insert')
def celery_review_insert(values):
    mysql_insert(review.table, values)
    return 'ok'


@celery_app.task(name='celery_handler.celery_review_get')
def celery_review_get(value):
    raw_data = mysql_select(review.table, "placeName", value)
    result = celery_review_response(raw_data)
    return result


@celery_app.task(name='celery_handler.celery_review_delete')
def celery_review_delete(value):
    mysql_delete(review.table, "traceID", value)
    return 'ok'


@celery_app.task(name='celery_handler.celery_review_like_update')
def celery_review_like_update(check, id):
    if check:
        mysql_update(review.table, "likeNum", "likeNum+1", "traceID", id)
    else:
        mysql_update(review.table, "likeNum", "likeNum-1", "traceID", id)
    raw_data = mysql_select(review.table, "traceID", id)
    result = celery_review_response(raw_data)
    return result


def celery_user_login(values):
    if redis_get(values['userId']):
        redis_expire(values['userId'])
        log_handler.user_login_session_check(log_msg['LOGIN_POST'], log_msg['SESSION_EXIST'], values['userId'])
        return 'session exist'
    elif len(celery_users_response(mysql_select(user.table, 'userId', values['userId']))['data']) == 0:
        mysql_insert(user.table, values)
        log_handler.user_login_register(log_msg['LOGIN_POST'], log_msg['USER_REGISTER'], values)
    values['userToken'] = get_auth_token(values['userId'])
    redis_set(values['userId'], values['userToken'])
    redis_expire(values['userId'])
    log_handler.user_login_session_made(log_msg['LOGIN_POST'], log_msg['SESSION_MADE'], values)
    return values


@celery_app.task(name='celery_handler.celery_user_logout')
def celery_user_logout(key):
    if redis_get(key):
        redis_delete(key)
        log_handler.user_logout(log_msg['LOGOUT_GET'], log_msg['SESSION_DISCONNECTION'], key)
        return 'ok'
    else:
        log_handler.error_log(log_msg['LOGOUT_GET'],log_msg['SESSION_NOT_EXIST'])
        return 'ok'


@celery_app.task(name='celery_handler.celery_user_dropout')
def celery_user_dropout(key):
    if mysql_select(user.table, 'userId', key):
        mysql_delete(user.table, 'userId', key)
        log_handler.user_dropout(log_msg['DROPOUT_DELETE'], log_msg['USER_DROPOUT'], key)
        return 'ok'
    else:
        log_handler.error_log(log_msg['DROPOUT_DELETE'], log_msg['ID_NOT_EXIST'])
        return 'ok'
