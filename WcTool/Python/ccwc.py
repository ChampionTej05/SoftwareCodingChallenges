#!/usr/bin/env python3
"""
This is the main file for the WcTool. We will implement Linux WC tool in
Python. We will use the argparse module to parse the command line arguments.

How to run: ccwc -lwcm filename 
filename: it is the name of the file to be processed 
"""

""" 
Notes:
The expression (bytes_data[byte_len] & 0xC0) == 0x80 is used to check whether a particular byte in a byte sequence is a continuation byte in a UTF-8 encoded string. Let's explain it step by step:

- bytes_data[byte_len]: This part accesses a specific byte in the bytes_data byte sequence. byte_len is a variable that keeps track of which byte we are currently examining in the sequence. It starts with 1 because the first byte in the sequence is typically processed separately.

- & (Bitwise AND): The & operator performs a bitwise AND operation. In this context, it's used to mask certain bits in the byte.

- 0xC0 and 0x80: These are hexadecimal values representing bit patterns. 0xC0 has the bit pattern 11000000, and 0x80 has the bit pattern 10000000.

Now, let's see how this expression evaluates for each byte in a byte sequence:

Start Byte (First Byte): When byte_len is 1, it corresponds to the first byte in a multi-byte sequence. Let's say this byte starts with 1110xxxx to indicate a three-byte character in UTF-8. When you perform (bytes_data[1] & 0xC0) == 0x80, you are essentially checking if the high-order bits of the first byte match the continuation byte pattern. In this case, the expression evaluates to False, indicating that it's not a continuation byte.

Continuation Byte (Subsequent Bytes): When byte_len is greater than 1, it corresponds to the subsequent bytes in a multi-byte sequence. These bytes always start with 10xxxxxx. When you perform (bytes_data[byte_len] & 0xC0) == 0x80, you are checking if the high-order bits of the byte match the continuation byte pattern. In this case, the expression evaluates to True, indicating that it's a continuation byte.

Here's an example byte sequence [0xE6, 0x81, 0xAF] again:

For the first byte 0xE6, (bytes_data[0] & 0xC0) == 0x80 evaluates to False, indicating it's not a continuation byte.
For the subsequent bytes 0x81 and 0xAF, (bytes_data[1] & 0xC0) == 0x80 and (bytes_data[2] & 0xC0) == 0x80 both evaluate to True, indicating that they are continuation bytes.

"""

# Importing the required modules
import argparse
import os
import subprocess
import locale
import unicodedata
import sys


# Defining the Constants

# Defining the worker functions

def is_multibyte_locale():
    """
    Check if the current locale supports multibyte characters.
    """
    try:
        encoding = locale.getlocale()[1]
        return "UTF-8" in encoding.upper()
    except (locale.Error, AttributeError):
        # Unable to determine locale information; assume not multibyte
        return False

def count_characters_or_bytes(file_path, use_multibyte):
    """
    Counts the number of characters or bytes in the file object provided.

    Args:
        file_path (str): The path to the file to be processed.
        use_multibyte (bool): A flag indicating whether to count characters or bytes. If True, counts characters. If False, counts bytes.

    Returns:
        int: The number of characters or bytes in the file, depending on the value of the use_multibyte flag.
    """
    if use_multibyte:
        return  count_char(file_path)
    else:
        return count_bytes(file_path)

        

def count_char(filepath):
    """
    Counts the number of characters in the file object provided.

    Args:
        filepath (str): The path to the file to be processed.

    Returns:
        int: The number of characters in the file.
    """
    #count characters in the file object provided
    file_object = open_file(filepath, "rb")
    if file_object is None:
        print("File not found.")
        return None

    bytes_data = file_object.read()
    close_file(file_object)
    character_count = 0
    while bytes_data:
        try:
            # checking the current element only assuming it is a single byte character
            # if this fails, it is a multibyte character 
            char = bytes_data[:1].decode("utf-8")
            character_count += 1

            bytes_data = bytes_data[1:]
        except UnicodeDecodeError:
            # skipping first byte as it is a multibyte character and it would be start byte
            byte_len =1
            # this is a multibyte character check loop for continuation bytes
            while byte_len < len(bytes_data) and (bytes_data[byte_len] & 0xC0) == 0x80:
                byte_len += 1
            character_count += 1
            bytes_data = bytes_data[byte_len:]
    return character_count

#doesn't work for multibyte characters
def count_characters(filepath):
    """
    Counts the number of characters in the file object provided.

    Args:
        filepath (str): The path to the file to be processed.

    Returns:
        int: The number of characters in the file.
    """
    #count characters in the file object provided
    file_object = open_file(filepath, "r", encoding="utf-8")
    if file_object is None:
        print("File not found.")
        return None

    file_characters = file_object.read()
    close_file(file_object)
    character_count = 0
    multibyte_characters = 0
    for character in file_characters:
        if unicodedata.east_asian_width(character) != "Na":
            # print(character)
            multibyte_characters += 1
        else:
            character_count += 1
    return character_count + multibyte_characters

def count_words(filepath):
    """
    Counts the number of words in the file object provided.

    Args:
        filepath (str): The path to the file to be processed.

    Returns:
        int: The number of words in the file.
    """
    #count words in the file object provided
    file_object = open_file(filepath, "r", encoding="utf-8")
    if file_object is None:
        print("File not found.")
        return None

    word_count = 0 
    for line in file_object:
        words = line.split()
        word_count += len(words)
    close_file(file_object)
    return word_count

def count_lines(filepath):

    """
    Counts the number of lines in the file object provided.

    Args:
        filepath (str): The path to the file to be processed.

    Returns:
        int: The number of lines in the file.
    """
    #count lines in the file object provided
    file_object = open_file(filepath, "r")
    if file_object is None:
        print("File not found.")
        return None

    # not-good for large files as it reads the entire file into memory
    #file_lines = file_object.readlines()

    line_count = sum(1 for line in file_object)
    close_file(file_object)
    return line_count

def count_bytes(filepath):
    """
    Counts the number of bytes in the file object provided.

    Args:
        filepath (str): The path to the file to be processed.

    Returns:
        int: The number of bytes in the file.
    """
    #count bytes in the file object provided
    file_object = open_file(filepath, "rb")
    if file_object is None:
        print("File not found.")
        return None

    file_bytes = file_object.read()
    close_file(file_object)
    return len(file_bytes)
    
def validate_command(command, expected_output):
    """
    Validates the command output against the expected output.

    Args:
        command (str): The command to be executed.
        expected_output (str): The expected output of the command.

    Returns:
        bool: True if the command output matches the expected output, False otherwise.
    """
    try:
        output = subprocess.check_output(command, shell=True)
        output = output.decode("utf-8").strip()
        # print("Output: ", output)
    except subprocess.CalledProcessError as e:
        print("Error executing command: ", command)
        print("Error message: ", e.output)
        return False
    else:
        expected_output = expected_output.strip()
        if output == expected_output:
            print("Command Output validated successfully.")
            return True
        else:
            print("Error executing command: ", command)
            print("Expected output: ", expected_output)
            print("Actual output: ", output)
            return False

def open_file(filepath, option, encoding="utf-8"):
    """
    Opens the file and returns the file object.

    Args:
        filepath (str): The path to the file to be opened.
        option (str): The mode in which the file is to be opened.

    Returns:
        file object: The file object if the file is found, None otherwise.
    """
    #open file and return file object
    try:
        # if binary mode, don't specify encoding
        if "b" in option:
            file_object = open(filepath, option)
        else:
            file_object = open(filepath, option, encoding=encoding)
    except FileNotFoundError:
        print("File not found.")
        return None
    else:
        return file_object
    
def close_file(file_object):
    """
    Closes the file object.

    Args:
        file_object (file object): The file object to be closed.

    Returns:
        None
    """
    #close file object
    file_object.close()
    return


# Defining the main function
def main():
    """
    The main function that parses the command line arguments and executes the appropriate command.
    """
    parser = argparse.ArgumentParser(description="This is a Python implementation of the Linux WC tool.")
    # nargs="?" means that the filename is optional, default="-" means that if no filename is provided, the program will read from STDIN
    parser.add_argument("filename", help="Name of the file to be processed.", nargs="?", default="-")
    parser.add_argument("-c", "--bytes", help="Count the number of bytes in the file.", action="store_true")
    parser.add_argument("-l", "--lines", help="Count the number of lines in the file.", action="store_true") 
    parser.add_argument("-w", "--words", help="Count the number of words in the file.", action="store_true") 
    parser.add_argument("-m", "--multibyte", help="Count characters (if locale supports multibyte), otherwise count bytes.", action="store_true")  
    args = parser.parse_args()

    cleanup_of_stdin = False

    if args.filename == "-":
        cleanup_of_stdin = True
        print("Reading from STDIN.")
        filename = "stdin.txt"
        filepath = os.path.join(os.getcwd(), filename)
        with open(filepath, "w") as f:
            for line in sys.stdin:
                f.write(line)
        
        args.filename = filename
    

    print("The name of the file is: ", args.filename)
    # filepath = os.path.join(INPUT_FILE_PATH_DIR, args.filename)
    current_dir = os.getcwd()
    filepath = os.path.join(current_dir, args.filename)
    print("Current directory is: ", current_dir)
    print("The filepath is: ", filepath)
    if args.bytes:
        file_bytes = count_bytes(filepath)
        expected_output = str(file_bytes) + " " + filepath + "\n"
        print("Command Output for No of Bytes: ", expected_output)
        command = "wc -c " + filepath
        validate_command(command, expected_output)
    if args.lines:
        file_lines = count_lines(filepath)
        expected_output = str(file_lines) + " " + filepath + "\n"
        print("Command Output for No of Lines: ", expected_output)
        command = "wc -l " + filepath
        validate_command(command, expected_output)
    if args.words:
        file_words = count_words(filepath)
        expected_output = str(file_words) + " " + filepath + "\n"
        print("Command Output for No of Words: ", expected_output)
        command = "wc -w " + filepath
        validate_command(command, expected_output)
    if args.multibyte:
        use_multibyte = is_multibyte_locale()
        print("Multibyte locale: ", use_multibyte)
        file_characters = count_characters_or_bytes(filepath, use_multibyte)
        expected_output = str(file_characters) + " " + filepath + "\n"
        print("Command Output for No of Characters: ", expected_output)
        command = "wc -m " + filepath
        validate_command(command, expected_output)
    
    # if no options are provided use default options
    if not (args.bytes or args.lines or args.words or args.multibyte):
        file_lines = count_lines(filepath)
        print("No of Lines: ", file_lines)
        expected_output = str(file_lines) + " " + filepath + "\n"
        command = "wc -l " + filepath
        validate_command(command, expected_output)

        file_bytes = count_bytes(filepath)
        print("No of Bytes: ", file_bytes)
        expected_output = str(file_bytes) + " " + filepath + "\n"
        command = "wc -c " + filepath
        validate_command(command, expected_output)

        file_words = count_words(filepath)
        print("No of Words: ", file_words)
        expected_output = str(file_words) + " " + filepath + "\n"
        command = "wc -w " + filepath
        validate_command(command, expected_output)

    if cleanup_of_stdin:    
        os.remove(filepath)



# Calling the main function
if __name__ == "__main__":
    main()