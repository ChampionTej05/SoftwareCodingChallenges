package main

import (
	"bufio"
	"bytes"
	"errors"
	"fmt"
	"io"
	"os"
	"os/exec"
	"path/filepath"
	"strconv"
	"strings"
	"unicode/utf8"
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

func CountCharacters(filepath string) (int, error) {
	contents, err := GetFileContents(filepath)
	if err != nil {
		// fmt.Println("Error getting file contents:", err)
		return 0, err
	}
	characterCount := 0
	i := 0

	for i < len(contents) {
		// Decode the next UTF-8 character (single or multi-byte)
		r, size := utf8.DecodeRune(contents[i:])

		// Handle invalid UTF-8 sequences
		if r == utf8.RuneError && size == 1 {
			// Invalid UTF-8 byte, skip it
			i++
			continue
		}

		// Valid character found
		characterCount++
		i += size
	}

	return characterCount, nil
}

func ValidateCommand(command string, expectedOutput string) bool {

	parts := strings.Fields(command)
	if len(parts) == 0 {

		fmt.Println("No command provided")
		return false
	}

	executable := parts[0]
	arguments := parts[1:]
	cmd := exec.Command(executable, arguments...)
	output, err := cmd.CombinedOutput()
	if err != nil {
		fmt.Println("Error running command:", err)
		return false
	}
	actualOutput := strings.TrimSpace(string(output))
	expectedOutput = strings.TrimSpace(expectedOutput)
	if actualOutput == expectedOutput {
		fmt.Println("Command output is correct")
		return true
	} else {
		fmt.Println("Command output is incorrect")
		fmt.Println("Expected:", expectedOutput)
		fmt.Println("Actual:", actualOutput)
		return false
	}

}

func main() {
	fmt.Println("Hello World!")

	var countBytes, countLines, countWords, countCharacters bool
	var cleanupTempFile bool = false
	// Parse command-line arguments manually
	commandLineArgs := os.Args

	fmt.Println("commandLineArgs:", commandLineArgs)

	if len(commandLineArgs) == 1 {
		fmt.Println("No command-line arguments provided")

		file, err := os.Create("temp_file.txt")
		if err != nil {
			fmt.Println("Error creating file:", err)
			return
		}
		defer file.Close()
		cleanupTempFile = true
		// copy input from stdin to file
		_, err = io.Copy(file, os.Stdin)
		if err != nil {
			fmt.Println("Error copying input to file:", err)
			return
		}
		fmt.Println("File created: temp_file.txt")
		os.Args = append(os.Args, "temp_file.txt")
	} else {
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
			if strings.Contains(arg, "m") {
				countCharacters = true
			}
		}
	}

	if !countBytes && !countLines && !countWords && !countCharacters {
		fmt.Println("Going with default Implementation")
		countLines = true
		countBytes = true
		countWords = true

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
		expectedOutput := strconv.Itoa(bytes) + " " + filename + "\n"
		ValidateCommand("wc -c "+filename, expectedOutput)
	}
	if countLines {
		fmt.Println("Counting lines...")
		lines, err := CountLines(filepath)
		if err != nil {
			fmt.Println("Error getting line count:", err)
			return
		}
		fmt.Println("Line count:", lines)
		expectedOutput := strconv.Itoa(lines) + " " + filename + "\n"
		ValidateCommand("wc -l "+filename, expectedOutput)
	}

	if countWords {
		fmt.Println("Counting words...")
		words, err := CountWords(filepath)
		if err != nil {
			fmt.Println("Error getting words count:", err)
			return
		}
		fmt.Println("Words count:", words)
		expectedOutput := strconv.Itoa(words) + " " + filename + "\n"
		ValidateCommand("wc -w "+filename, expectedOutput)
	}

	if countCharacters {
		fmt.Println("Counting characters...")
		characters, err := CountCharacters(filepath)
		if err != nil {
			fmt.Println("Error getting characters count:", err)
			return
		}
		fmt.Println("Characters count:", characters)
		expectedOutput := strconv.Itoa(characters) + " " + filename + "\n"
		ValidateCommand("wc -m "+filename, expectedOutput)
	}

	if cleanupTempFile {
		err = os.Remove("temp_file.txt")
		if err != nil {
			fmt.Println("Error removing temp file:", err)
			return
		}
	}
}
