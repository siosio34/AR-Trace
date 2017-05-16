# -*- coding: utf-8 -*-
from init.redis_db import get_conn, close_conn
from common.const import const_values
import time

def redis_get(key):
    conn = get_conn()
    result = conn.get(key)
    close_conn(conn)
    return result


def redis_set(key, value):
    conn = get_conn()
    conn.set(key, value)
    close_conn(conn)
    return


def redis_delete(key):
    conn = get_conn()
    conn.delete(key)
    close_conn(conn)
    return


def redis_expire(key):
    conn = get_conn()
    conn.expire(key, const_values['TTL'])
    return
