import java.io.*;
import java.util.Base64;

/** This class has been written to make file input and output operations easier to write and read. **/
public class FileIO {

    /** This function has been written to show the functions used in the program as a whole. **/
    public void fileHandler(String[] args) throws Exception {

        String[] files = {"", "", "", ""};    // 0 --> keyFile , 1 --> inputFile , 2 --> outputFile , 3 --> mode
        parameterSelection(files, args);                                                    // Parameter Select part
        String binaryKey = new String(Base64.getDecoder().decode(readFile(files[0])));      // Decoding Base64 keyFile
        String input = readFile(files[1]);                                                  // Reading inputFile
        while(input.length() % 96 != 0) {
            input=input.concat("0");                    // Completes the bits given in input file to multiple of 96.
        }
        PrintWriter output = new PrintWriter(files[2]);                                     // Creating outputFile
        caseSelection(args, files, input, binaryKey, output);
        output.close();
    }

    /** This function was written to set program arguments from the command line.
     * Parameters might be given in different orders on the command line.
     * @param files --> Exists to save the contents of files **/
    private void parameterSelection(String[] files, String[] args) {

        for (int i = 1; i < args.length; i += 2) {
            switch (args[i]) {
                case "-K":
                    files[0] = args[i + 1];
                    break;
                case "-I":
                    files[1] = args[i + 1];
                    break;
                case "-O":
                    files[2] = args[i + 1];
                    break;
                case "-M":
                    files[3] = args[i + 1];
                    break;
                default:
                    System.out.println("Wrong Parameter!");      // ERROR
                    System.exit(0);                       // Terminate program
                    break;
            }
        }
    }

    /** This function is written for file reading.
     * Converting and saving the incoming data to byte type using FileInputStream.
     * @param fileName --> File name to read **/
    private String readFile(String fileName) throws IOException {
        File file = new File(fileName);
        FileInputStream fileInputStream = new FileInputStream(fileName);

        byte[] data = new byte[(int) file.length()];
        fileInputStream.read(data);
        fileInputStream.close();
        return new String(data);
    }



    /** This function calls the necessary methods according to the parameters coming from the command line.
     * Converting and saving the incoming data to byte type using FileInputStream.
     * @param args --> Command line arguments
     * @param files --> File types
     * @param input --> input String
     * @param binaryKey --> binaryKey String
     * @param output --> output File **/
    private void caseSelection(String[] args, String[] files, String input, String binaryKey, PrintWriter output) {

        CryptFunctions CFuncs = new CryptFunctions();               // Creating CryptFunctions object

        if (args[0].equals("enc")) {
            StringBuilder encryptedString = new StringBuilder();    // String will be encrypted
            switch (files[3]) {
                case "ECB":
                    for (int i = 0; i < input.length(); i += 96) {
                        encryptedString.append(CFuncs.encryption(input.substring(i, i + 96), binaryKey));
                    }
                    break;
                case "CBC":
                    String C0 = "";
                    for (int i = 0; i < 96; i++) {
                        C0 = C0.concat("1");                        // String concatenated with 1's
                    }
                    for (int i = 0; i < input.length(); i += 96) {
                        C0 = CryptFunctions.xor(input.substring(i, i + 96), C0);
                        String tempEncrypted = CFuncs.encryption(C0, binaryKey);
                        encryptedString.append(tempEncrypted);
                        C0 = tempEncrypted;
                    }
                    break;
                case "OFB":
                    String X0 = "";
                    CFuncs.modeOFB(binaryKey, input, encryptedString, X0);
                    break;
                default:
                    System.out.println("Wrong Parameter!");         // ERROR
                    System.exit(0);                          // Terminate program
                    break;
            }
            output.print(encryptedString);
        } else if (args[0].equals("dec")) {
            StringBuilder decryptedString = new StringBuilder();
            switch (files[3]) {
                case "ECB":
                    for (int i = 0; i < input.length(); i += 96)
                        decryptedString.append(CFuncs.decryption(input.substring(i, i + 96), binaryKey));
                    break;
                case "CBC":
                    String C0 = "";
                    for (int i = 0; i < 96; i++) {
                        C0 = C0.concat("1");        // String concatenated with 1's
                    }
                    for (int i = 0; i < input.length(); i += 96) {
                        decryptedString.append(CryptFunctions.xor(C0, CFuncs.decryption(input.substring(i, i + 96), binaryKey)));
                        C0 = input.substring(i, i + 96);
                    }
                    break;
                case "OFB":
                    String X0 = "";
                    CFuncs.modeOFB(binaryKey, input, decryptedString, X0);
                    break;
                default:
                    System.out.println("Wrong Parameter!");      // ERROR
                    System.exit(0);                       // Terminate program
                    break;

            }
            output.print(decryptedString);
        }
        else{
            System.out.println("Wrong Parameter!");             // ERROR
            System.exit(0);                              // Terminate program
        }
    }
}