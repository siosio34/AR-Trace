# -*- coding: utf-8 -*-
from init import review, user


def review_insert_sql(values):
    items = review.to_list(values)
    query = "INSERT INTO review VALUES ("
    for item in items:
        if isinstance(item, basestring):
            query = query + '"' + item + '",'
        else:
            query = query + str(item) + ","
    query = query[:-1]
    query = query + ");"
    return query


def users_insert_sql(values):
    items = user.to_list(values)
    query = "INSERT INTO users VALUES ("
    for item in items:
        if isinstance(item, basestring):
            query = query + '"' + item + '",'
        else:
            query = query + str(item) + ","
    query = query[:-1]
    query = query + ");"
    return query


def select_sql(table, column, value):
    query = "SELECT * FROM {0} WHERE {1} = '{2}'".format(table, column, value)
    return query


def count_sql(table, column, value):
    query = "SELECT count(*) FROM {0} WHERE {1} = '{2}'".format(table, column, value)
    return query


def delete_sql(table, column, value):
    query = "DELETE FROM {0} WHERE {1} = '{2}'".format(table, column, value)
    return query


def update_sql(table, target_column, target_value, column, value):
    query = "UPDATE {0} SET {1} = {2} WHERE {3} = '{4}'".format(table, target_column, target_value, column, value)
    return query
