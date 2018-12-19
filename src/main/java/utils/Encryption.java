package utils;

public final class Encryption {

  public static String encryptDecryptXOR(String rawString) {

    // If encryption is enabled in Config.
    if (Config.getEncryption()) {

      // The key is predefined and hidden in code
      // TODO: Create a more complex code and store it somewhere better :FIX
      // Kode er gemt i config.json, da den ikke kan læse i war filen.
      char[] key = Config.getEncryptionKey();

      // Stringbuilder enables you to play around with strings and make useful stuff
      StringBuilder thisIsEncrypted = new StringBuilder();

      // TODO: This is where the magic of XOR is happening. Are you able to explain what is going on? :FIX
      // Dette er forklaret dybbere i rapporten, men kort sagt så bliver alt lavet om til binær.
      // Det bliver en plaintext i binær som ligges sammen med en secret key og giver en ciphertext.
      // Denne kan dekodes ved at ligge den secret key til ciphertext, som giver plaintext.
      for (int i = 0; i < rawString.length(); i++) {
        thisIsEncrypted.append((char) (rawString.charAt(i) ^ key[i % key.length]));
      }

      // We return the encrypted string
      return thisIsEncrypted.toString();

    } else {
      // We return without having done anything
      return rawString;
    }
  }
}
