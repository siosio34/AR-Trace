# -*- coding: utf-8 -*-
import sys
import os

proj_path = os.path.abspath(os.path.dirname(__file__))

sys.path.append(proj_path)
print '01'
sys.path.append(proj_path+'/init')
print '02'
sys.path.append(proj_path+'/common')
print '03'
sys.path.append(proj_path+'/models')
sys.path.append(proj_path+'/tasks')
sys.path.append(proj_path+'/utils')

print sys.path

