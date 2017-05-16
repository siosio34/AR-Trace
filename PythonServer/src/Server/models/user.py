# -*- coding: utf-8 -*-
class User:
    def __init__(self):
        self.table = "users"
        pass

    def to_list(self, values):

        user_list = list()
        user_list.append(values["userId"])
        user_list.append(values["userName"])
        user_list.append(values["userEmail"])
        user_list.append(values["userImageURL"])
        user_list.append(values["userToken"])
        return user_list

    def to_json(self, values):
        user_json = {"userId": values.userId,
                     "userName": values.userName,
                     "userEmail": values.userEmail,
                     "userImageURL": values.userImageURL,
                     "userToken": values.userToken
                     }

        return user_json
