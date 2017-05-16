# -*- coding: utf-8 -*-
from common.const import const_values
from itsdangerous import URLSafeTimedSerializer

login_serializer = URLSafeTimedSerializer(const_values['TOKEN_KEY'])


def get_auth_token(user_id):
    data = [str(user_id)]
    return login_serializer.dumps(data)
