import unittest

from doubler import Doubler

class TestDoubler(unittest.TestCase):
    
    def setUp(self):
        self.doubler = Doubler()

    def test_triples(self):
        self.assertEquals(6, self.doubler.double(2))
