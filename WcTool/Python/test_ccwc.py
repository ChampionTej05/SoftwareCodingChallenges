import unittest
import subprocess
import os
from ccwc import *

class TestWC(unittest.TestCase):
    def setUp(self):
        self.test_file = "test_file.txt"
        with open(self.test_file, "w") as f:
            f.write("This is a test file.\nIt has multiple lines.\nIt also has multiple words.\nSun Tzŭ in the 82 _p’ien_.\n")

    def tearDown(self):
        # os.remove(self.test_file)
        pass

    def test_count_bytes(self):
        self.assertEqual(count_bytes(self.test_file), 102)

    def test_count_lines(self):
        self.assertEqual(count_lines(self.test_file), 4)

    def test_count_words(self):
        self.assertEqual(count_words(self.test_file), 20)

    def test_is_multibyte_locale(self):
        self.assertEqual(is_multibyte_locale(), True)

    def test_count_characters_or_bytes(self):
        is_multibyte_locale = True
        self.assertEqual(count_characters_or_bytes(self.test_file, is_multibyte_locale ), 99)

    def test_validate_command(self):
        self.assertEqual(validate_command("echo 'test'", "test\n"), True)

if __name__ == '__main__':
    unittest.main()