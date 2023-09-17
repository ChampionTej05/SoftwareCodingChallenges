package main

import (
	"bufio"
	"bytes"
	"errors"
	"fmt"
	"io"
	"os"
	"path/filepath"
	"strings"
)

var ErrFileNotFound error = errors.New("file not found")

func GetFileContents(filepath string) ([]byte, error) {

	file, err := os.Open(filepath)
	if err != nil {

		fmt.Println("Error opening file:", err)
		if os.IsNotExist(err) {
			return nil, ErrFileNotFound
		}
		return nil, err
	}

	defer file.Close()

	data, err := io.ReadAll(file)
	if err != nil {
		fmt.Println("Error reading file:", err)
		return nil, err
	}
	return data, nil
}

func CountBytes(filepath string) (int, error) {

	contents, err := GetFileContents(filepath)
	if err != nil {
		// fmt.Println("Error getting file contents:", err)
		return 0, err
	}
	return len(contents), nil
}

func CountLines(filepath string) (int, error) {
	contents, err := GetFileContents(filepath)
	if err != nil {
		// fmt.Println("Error getting file contents:", err)
		return 0, err
	}
	// Scanner splits on newlines by default and returns each line
	scanner := bufio.NewScanner(bytes.NewReader(contents))

	var linesCount int = 0

	for scanner.Scan() {
		linesCount++
	}

	// If there was an error during scanning, such as a malformed input, it will be reported here.
	if err := scanner.Err(); err != nil {
		fmt.Println("Error scanning file:", err)
		return 0, err
	}
	return linesCount, nil
}

func CountWords(filepath string) (int, error) {
	contents, err := GetFileContents(filepath)
	if err != nil {
		// fmt.Println("Error getting file contents:", err)
		return 0, err
	}

	scanner := bufio.NewScanner(bytes.NewReader(contents))

	wordsCount := 0

	for scanner.Scan() {
		wordsSlice := strings.Fields(scanner.Text())
		wordsCount += len(wordsSlice)
	}

	// If there was an error during scanning, such as a malformed input, it will be reported here.
	if err := scanner.Err(); err != nil {
		fmt.Println("Error scanning file:", err)
		return 0, err
	}
	return wordsCount, nil
}

func main() {
	fmt.Println("Hello World!")

	var countBytes, countLines, countWords bool
	// Parse command-line arguments manually
	commandLineArgs := os.Args
	for _, arg := range os.Args[1 : len(commandLineArgs)-1] {
		fmt.Println("arg:", arg)
		if strings.Contains(arg, "c") {
			countBytes = true
		}
		if strings.Contains(arg, "l") {
			countLines = true
		}
		if strings.Contains(arg, "w") {
			countWords = true
		}
	}

	if !countBytes && !countLines && !countWords {
		fmt.Println("Usage: ccwc [-l] [-c] [-w] <filename>")
		os.Exit(1)
	}

	filename := os.Args[len(os.Args)-1]

	cwd, err := os.Getwd()
	if err != nil {
		fmt.Println("Error getting current working directory:", err)
		return
	}
	fmt.Println("Current working directory:", cwd)
	filepath := filepath.Join(cwd, filename)
	fmt.Println("File path:", filepath)

	// contents, err := GetFileContents(filepath)
	// if err != nil {
	// 	fmt.Println("Error getting file contents:", err)
	// 	return
	// }
	// fmt.Println("File contents:", string(contents))

	if countBytes {
		fmt.Println("Counting bytes...")
		bytes, err := CountBytes(filepath)
		if err != nil {
			fmt.Println("Error getting byte count:", err)
			return
		}
		fmt.Println("Byte count:", bytes)
	}
	if countLines {
		fmt.Println("Counting lines...")
		lines, err := CountLines(filepath)
		if err != nil {
			fmt.Println("Error getting line count:", err)
			return
		}
		fmt.Println("Line count:", lines)
	}

	if countWords {
		fmt.Println("Counting words...")
		words, err := CountWords(filepath)
		if err != nil {
			fmt.Println("Error getting words count:", err)
			return
		}
		fmt.Println("Words count:", words)
	}
}
