import unittest

from package import BasicObject

class TestBasicObject(unittest.TestCase):
    def test_says_message(self):
        basic_object = BasicObject('Pythonland')
        self.assertEquals('Hello, Pythonland', basic_object.getMessage())
