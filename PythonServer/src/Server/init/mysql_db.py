# -*- coding: utf-8 -*-
from sqlalchemy import create_engine


engine = create_engine('mysql://ayoung:ayoung@192.168.1.251:3306/ayoung',
                       convert_unicode=False,
                       pool_size=100,
                       max_overflow=0,
                       pool_recycle=3600,
                       encoding='utf-8'
                       )


def get_conn():
    return engine.connect()


def close_conn(conn):
    conn.close()
    return
