package main

import (
	"bytes"
	"errors"
	"os"
	"testing"
)

var testFilePath = "test_file.txt"
var emptyTestFilePath = "empty_test_file.txt"
var testFileContents = []byte("This is a test file.\nIt has multiple lines.\nIt also has multiple words.\nSun Tzŭ in the 82 _p’ien_.\n")

func TestMain(m *testing.M) {

	err := os.WriteFile(testFilePath, testFileContents, 0644)
	if err != nil {
		panic(err)
	}

	// Test with an empty file
	err = os.WriteFile(emptyTestFilePath, []byte{}, 0644)
	if err != nil {
		panic(err)
	}

	// Run the tests
	exitCode := m.Run()

	// Tear down the test file
	err = os.Remove(testFilePath)
	if err != nil {
		panic(err)
	}

	// Tear down the empty test file
	err = os.Remove(emptyTestFilePath)
	if err != nil {
		panic(err)
	}

	os.Exit(exitCode)

}

func TestGetFileContents(t *testing.T) {
	// Test with a valid file
	expectedContents := testFileContents
	contents, err := GetFileContents("test_file.txt")
	if err != nil {
		t.Errorf("GetFileContents returned an error: %v", err)
	}
	if !bytes.Equal(contents, expectedContents) {
		t.Errorf("GetFileContents returned incorrect contents: expected %v, got %v", expectedContents, contents)
	}

	// Test with a non-existent file
	_, err = GetFileContents("nonexistent_file.txt")
	if !errors.Is(err, ErrFileNotFound) {
		t.Errorf("GetFileContents did not return expected error for non-existent file: expected %v, got %v", ErrFileNotFound, err)
	}
}

func TestCountBytes(t *testing.T) {
	// Test with a valid file
	expectedCount := 102
	count, err := CountBytes("test_file.txt")
	if err != nil {
		t.Errorf("CountBytes returned an error: %v", err)
	}
	if count != expectedCount {
		t.Errorf("CountBytes returned incorrect count: expected %d, got %d", expectedCount, count)
	}

	// Test with a non-existent file
	_, err = CountBytes("nonexistent_file.txt")
	if !errors.Is(err, ErrFileNotFound) {
		t.Errorf("CountBytes did not return expected error for non-existent file: expected %v, got %v", ErrFileNotFound, err)
	}

	count, err = CountBytes(emptyTestFilePath)
	if err != nil {
		t.Errorf("CountBytes returned an error for empty file: %v", err)
	}
	if count != 0 {
		t.Errorf("CountBytes returned incorrect count for empty file: expected 0, got %d", count)
	}

}

func TestCountLines(t *testing.T) {
	// Test with a valid file
	expectedCount := 4
	count, err := CountLines("test_file.txt")
	if err != nil {
		t.Errorf("CountLines returned an error: %v", err)
	}
	if count != expectedCount {
		t.Errorf("CountLines returned incorrect count: expected %d, got %d", expectedCount, count)
	}

	// Test with a non-existent file
	_, err = CountLines("nonexistent_file.txt")
	if !errors.Is(err, ErrFileNotFound) {
		t.Errorf("CountLines did not return expected error for non-existent file: expected %v, got %v", ErrFileNotFound, err)
	}

	count, err = CountLines(emptyTestFilePath)
	if err != nil {
		t.Errorf("CountLines returned an error for empty file: %v", err)
	}
	if count != 0 {
		t.Errorf("CountLines returned incorrect count for empty file: expected 0, got %d", count)
	}
}

func TestCountWords(t *testing.T) {
	// Test with a valid file
	expectedCount := 20
	count, err := CountWords("test_file.txt")
	if err != nil {
		t.Errorf("CountWords returned an error: %v", err)
	}
	if count != expectedCount {
		t.Errorf("CountWords returned incorrect count: expected %d, got %d", expectedCount, count)
	}

	// Test with a non-existent file
	_, err = CountWords("nonexistent_file.txt")
	if !errors.Is(err, ErrFileNotFound) {
		t.Errorf("CountWords did not return expected error for non-existent file: expected %v, got %v", ErrFileNotFound, err)
	}

	count, err = CountWords(emptyTestFilePath)
	if err != nil {
		t.Errorf("CountWords returned an error for empty file: %v", err)
	}
	if count != 0 {
		t.Errorf("CountWords returned incorrect count for empty file: expected 0, got %d", count)
	}
}

func TestCountCharacters(t *testing.T) {
	// Test with a valid file
	expectedCount := 99
	count, err := CountCharacters("test_file.txt")
	if err != nil {
		t.Errorf("CountCharacters returned an error: %v", err)
	}
	if count != expectedCount {
		t.Errorf("CountCharacters returned incorrect count: expected %d, got %d", expectedCount, count)
	}

	// Test with a non-existent file
	_, err = CountCharacters("nonexistent_file.txt")
	if !errors.Is(err, ErrFileNotFound) {
		t.Errorf("CountCharacters did not return expected error for non-existent file: expected %v, got %v", ErrFileNotFound, err)
	}

	count, err = CountCharacters(emptyTestFilePath)
	if err != nil {
		t.Errorf("CountCharacters returned an error for empty file: %v", err)
	}
	if count != 0 {
		t.Errorf("CountCharacters returned incorrect count for empty file: expected 0, got %d", count)
	}
}
