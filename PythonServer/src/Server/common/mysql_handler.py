# -*- coding: utf-8 -*-
from init.mysql_db import get_conn, close_conn
from utils.make_sql import *
from init import review, user


def mysql_insert(table, values):
    conn = get_conn()
    if table is review.table:
        query = review_insert_sql(values)
    elif table is user.table:
        query = users_insert_sql(values)
    else:
        return
    conn.execute(query)
    close_conn(conn)
    return


def mysql_select(table, column, value):
    conn = get_conn()
    query = select_sql(table, column, value)
    result = conn.execute(query)
    close_conn(conn)
    return result

def mysql_count(table, column, value):
    conn = get_conn()
    query = count_sql(table, column, value)
    result = conn.execute(query)
    close_conn(conn)
    return result


def mysql_delete(table, column, value):
    conn = get_conn()
    query = delete_sql(table, column, value)
    conn.execute(query)
    close_conn(conn)
    return


def mysql_update(table, target_column, target_value, column, value):
    conn = get_conn()
    query = update_sql(table, target_column, target_value, column, value)
    result = conn.execute(query)
    close_conn(conn)
    return result
