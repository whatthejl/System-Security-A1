import java.math.*; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException; 
import java.io.*; 
import java.util.*;
import java.nio.file.Paths;
import static java.util.stream.Collectors.*; 
import static java.util.Map.Entry.*; 

class Rainbow
{
	static Scanner input;
	static Formatter output;
	static HashMap<String, String> rainbowTable = new HashMap<String, String>();
	static ArrayList<String> password = new ArrayList<>();
	static ArrayList<String> used = new ArrayList<>();
	static int length = 0;
	
	public static void main(String args[]) throws NoSuchAlgorithmException, FileNotFoundException  
	{
		Scanner sc = new Scanner(System.in);		
		String fileName = args[0];
		String s = "";	
		boolean error = false;
		
		try
		{	
			input = new Scanner (Paths.get (fileName));
		}
		catch (FileNotFoundException e)
		{
			System.err.println ("Error in open the file");
			error = true;
		}
		catch (IOException e)
		{
			System.err.println ("Error in IO");
			error = true;
		}
		
		try
		{
			while(input.hasNext())
			{
				s = input.nextLine();
				password.add(s);
				length++;
			}
		}
		catch(Exception e)
		{
			System.out.println("Error in reading password file.");
			error = true;
		}
		if(input != null)
		{
			input.close();
		}
		
		if(!error)
		{
			System.out.println("No. Of Passwords: " + length);
			
			//store rainbow table
			for (int i = 0; i < length; i++)
			{
				if (!used.contains(password.get(i)))
				{
					int k = i;
					String hash = "";
					for (int j = 0; j < 5; j++)
					{
						String pw = password.get(k);
						hash = getMd5(pw);
						k = reduction(hash);
						if (!used.contains(pw))
						{
							used.add(pw);
						}
						//hash = hash pw
						//reduce the hash of pw
						//k = reduced int
						//put pw in used arraylist
					}
					rainbowTable.put(
					password.get(i), hash);
					//store password.get(i) the original pw with the final hash in the rainbow table
				}
			}
			rainbowTable = sortByValue(rainbowTable);
			createFile("Rainbow.txt");
			
			boolean check = false;
			Scanner in = new Scanner(System.in);
			do
			{
				System.out.print("Please enter a hash value(Q to quit): ");
				String userInput = in.nextLine();
				if (userInput.equals("q") || userInput.equals("Q"))
				{
					check = true;
				}
				else
				{
					if (userInput.length() == 32)
					{
						String pass = verify(userInput);
						if (pass == "negative")
						{
							System.out.println("Password is not in list.");
						}
						else
						{
							System.out.println("Password is " + pass + "\n");
						}
					}
					else
					{
						System.out.print("Please enter a hash value.");
					}
				}
			} while (check == false);
		}
	}
    
	public static String getMd5(String input) 
	{ 
		try 
		{ 
			// Static getInstance method is called with hashing MD5 
			MessageDigest md = MessageDigest.getInstance("MD5"); 

			// digest() method is called to calculate message digest 
			//  of an input digest() return array of byte 
			byte[] messageDigest = md.digest(input.getBytes()); 

			// Convert byte array into signum representation 
			BigInteger no = new BigInteger(1, messageDigest); 

			// Convert message digest into hex value 
			String hashtext = no.toString(16); 
			while (hashtext.length() < 32) 
			{ 
				hashtext = "0" + hashtext; 
			} 
			return hashtext; 
		} 

		// For specifying wrong message digest algorithms 
		catch (NoSuchAlgorithmException e)
		{ 
			throw new RuntimeException(e); 
		} 
	} 
	// converts hash to int then returns mod
	public static int reduction(String hash)
	{
		BigInteger bigInt = new BigInteger(hash, 16);
		BigInteger len = BigInteger.valueOf(length);
		int result = (bigInt.mod(len)).intValue();
		return result;
	}
	// sorts rainbow table by keys in ascending order
	public static HashMap<String, String> sortByValue (HashMap<String, String> map) 
	{ 
		HashMap<String, String> sorted = map.entrySet().stream().sorted(comparingByValue()).collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));

		return sorted;
	}
	//create rainbowtable.txt
	public static void createFile (String name)
	{
		try
		{
			File myObj = new File (name);
			if (myObj.createNewFile())
			{
				System.out.println("File Created: " + myObj.getName() + "\n");
			}
			else
			{
				System.out.println("File Already Exists.\n");
			}
		}
		catch (IOException e)
		{
			System.err.println ("An error occurred.");
		}
		//write to file
		int i = 1;
		try
		{
			FileWriter myWriter = new FileWriter(name);
			for (Map.Entry <String, String> entry : rainbowTable.entrySet())
			{
				myWriter.write(i + " " + entry.getKey() + " " + entry.getValue() + "\n");
				i++;		
			}
		}
		catch (IOException e)
		{
			System.out.println("An error occurred.");
		}
	} 
	//check if hash of password is in the password list
	public static String verify (String hash)
	{
		String pw = "";
		String temphash = "";
		int index;
		
		for (String i : rainbowTable.keySet())
		{
			if (getMd5(i).equals(hash))
			{
				return i;
			}
		}
		if (rainbowTable.containsValue(hash))
		{
			for (String key : getKeys(rainbowTable, hash))
			{
				pw = key;
				break;
			}
			for (int j = 0; j < 4; j++)
			{
				temphash = getMd5(pw);
				index = reduction(temphash);
				pw = password.get(index);
			}
			return pw;
		}
		else
		{
			temphash = hash;
			for (int j = 0; j < 7; j++)
			{
				index = reduction(temphash);
				pw = password.get(index);
				temphash = getMd5(pw);
				if (temphash.equals(hash))
				{
					return pw;
				}
			}
			return "negative";
		}
	}
	//finding the key based on the values
	private static Set<String> getKeys(Map<String, String> map, String value)
	{
		Set<String> result = new HashSet<>();
		if (map.containsValue(value)) {
			for (Map.Entry<String, String> entry : map.entrySet())
			{
				if (Objects.equals(entry.getValue(), value))
				{
					result.add(entry.getKey());
				}
			}
		}
		return result;
	}
}