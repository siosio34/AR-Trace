from sqlalchemy import create_engine
import sqlalchemy.pool as pool

from sqlalchemy.orm import scoped_session, sessionmaker

engine = create_engine('mysql://root:3456@192.168.1.206:3306/mydb',
                       convert_unicode=False,
                       #pool_size=100,
                       #max_overflow=20,
                       #pool_recycle=3600
                       )

engine_user = create_engine('mysql://root:3456@192.168.1.206:3306/user', convert_unicode=False)


					   
					   
					   
#db_session = scoped_session(sessionmaker(bind=engine,
#                                         autocommit=False,
#                                         autoflush=False))


