## Golang

To make your go utility run as a Shell command, follow the steps 

1. Name the go file as ccwc.go 
2. Implement main function in the file 
3. Create build using `go build -o ccwc`
4. Make it executable `chmod +x ccwc`
5. Copy it to bin `sudo mv ccwc /usr/local/bin/`
6. Run it now as a command `ccwc -c input.txt`