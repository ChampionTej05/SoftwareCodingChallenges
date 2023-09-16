#!/usr/bin/env python3

"""
This is the main file for the WcTool. We will implement Linux WC tool in
Python. We will use the argparse module to parse the command line arguments.
"""

# Importing the required modules
import argparse
import os
import subprocess


# Defining the Constants

INPUT_FILE_PATH_DIR = "/Users/rkathawate/Desktop/Rakshit_Projects/SoftwareCodingChallenges/WcTool"

# Defining the worker function 

def worker(name):
    print("The name of the file is: ", name)
    return

def count_bytes(filepath):
    #count bytes in the file object provided
    file_object = open_file(filepath, "rb")
    if file_object is None:
        print("File not found.")
        return None

    file_bytes = file_object.read()
    close_file(file_object)
    return len(file_bytes)
    
def validate_command(command, expected_output):
    #validate command
    try:
        output = subprocess.check_output(command, shell=True)
        print("Output: ", output)
    except subprocess.CalledProcessError as e:
        print("Error executing command: ", command)
        print("Error message: ", e.output)
        return False
    else:
        if output.decode("utf-8") == expected_output:
            return True
        else:
            print("Error executing command: ", command)
            print("Expected output: ", expected_output)
            print("Actual output: ", output.decode("utf-8"))
            return False

def open_file(filepath, option):
    #open file and return file object
    try:
        file_object = open(filepath, option)
    except FileNotFoundError:
        print("File not found.")
        return None
    else:
        return file_object
    
def close_file(file_object):
    #close file object
    file_object.close()
    return


# Defining the main function
def main():
    parser = argparse.ArgumentParser(description="This is a Python implementation of the Linux WC tool.")
    parser.add_argument("filename", help="Name of the file to be processed.")
    parser.add_argument("-c", "--bytes", help="Count the number of bytes in the file.", action="store_true")
    args = parser.parse_args()
    print("The name of the file is: ", args.filename)
    filepath = os.path.join(INPUT_FILE_PATH_DIR, args.filename)
    print("The filepath is: ", filepath)
    if args.bytes:
        file_bytes = count_bytes(filepath)
        print("The number of bytes in the file is: ", file_bytes)
        command = "wc -c " + filepath
        validate_command(command, file_bytes)


# Calling the main function
if __name__ == "__main__":
    main()