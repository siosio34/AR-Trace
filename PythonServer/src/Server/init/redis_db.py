# -*- coding: utf-8 -*-
import redis

redis_engine = redis.ConnectionPool(host='192.168.1.207', port=6379, max_connections=100)


def get_conn():
    #conn = redis_engine.get_connection(abs(1))
    conn = redis.Redis(connection_pool=redis_engine)
    return conn


def close_conn(conn):
    #redis_engine.release(conn)
    return
