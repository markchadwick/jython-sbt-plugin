import unittest

from doubler import Doubler

class TestDoubler(unittest.TestCase):
    
    def setUp(self):
        self.doubler = Doubler()

    def test_doubles(self):
        self.assertEquals(4, self.doubler.double(2))
