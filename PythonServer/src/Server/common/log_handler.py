# -*- coding: utf-8 -*-
from init import logger


def write_flush(logs):
    logger.write(logs)
    logger.flush()
    return


class LogHandler:
    def __init__(self):
        pass

    def error_log(self, stat, msg):
        logs = "{} {}\n".format(stat, msg)
        write_flush(logs)
        return

    def user_login_session_check(self, stat, msg, data):
        logs = "{} {} id={}\n".format(stat, msg, data)
        write_flush(logs)
        return


    def user_login_register(self, stat, msg, data):
        logs = "{} {} id={} name={} email={} image_url={}\n".format(stat, msg, data["userId"], data["userName"],
                                                                    data["userEmail"], data["userImageURL"])
        write_flush(logs)
        return

    def user_login_session_made(self, stat, msg, data):
        logs = "{} {} id={} token={}\n".format(stat, msg, data["userId"], data["userToken"])
        write_flush(logs)
        return

    def user_logout(self, stat, msg, data):
        logs = "{} {} id={}\n".format(stat, msg, data)
        write_flush(logs)
        return

    def user_dropout(self, stat, msg, data):
        logs = "{} {} id={}\n".format(stat, msg, data)
        write_flush(logs)
        return
