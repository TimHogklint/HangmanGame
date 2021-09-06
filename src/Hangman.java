import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

public class Hangman
{

    public static void main(String[] args)
    {
        int width = 7;
        int height = 6;

        // <editor-fold defaultstate="collapsed" desc="Graphics">
        char[] graph1 = new char[]{'*','*','*','|','*','*','*',
                                    '*','*','*','|','*','*','*',
                                    '*','*','*','*','*','*','*',
                                    '*','*','*','*','*','*','*',
                                    '*','*','*','*','*','*','*',
                                    '*','*','*','*','*','*','*'};

        char[] graph2 = new char[]{'*','*','*','|','*','*','*',
                                    '*','*','*','O','*','*','*',
                                    '*','*','*','|','*','*','*',
                                    '*','*','*','|','*','*','*',
                                    '*','*','*','*','*','*','*',
                                    '*','*','*','*','*','*','*'};

        char[] graph3 = new char[]{'*','*','*','|','*','*','*',
                                    '*','*','*','O','*','*','*',
                                    '*','*','*','|','*','*','*',
                                    '*','*','*','|','*','*','*',
                                    '*','*','/','*','\\','*','*',
                                    '*','/','*','*','*','\\','*'};


        char[] graph4 = new char[]{'*','*','*','|','*','*','*',
                                    '*','*','*','O','*','*','*',
                                    '*','-','-','|','-','-','*',
                                    '*','*','*','|','*','*','*',
                                    '*','*','/','*','\\','*','*',
                                    '*','/','*','*','*','\\','*'};

        // </editor-fold>

        // Poolen av ord som vi kan gissa på
        String[] guessWords = new String[]{"Snake","Zebra","Lion","Alligator","Owl","Cat","Dog","Chicken","Rat","Parrot"
                                           , "Shark", "Puma", "Goldfish", "Kangaroo", "Monkey", "Gorilla", "Ferret", "Horse"};
        boolean isGame = true;

        int playerLife = 3;
        int correctAwnsers = 0;

        String keyword = "";
        String hiddenKeyword = "";

        ArrayList<Character> previousGuesses = new ArrayList<>();

        Scanner readKey = new Scanner(System.in);

        while(isGame)
        {
            // we have word to guess
            if(keyword.length() > 0)
            {
                // while player is alive , let him guess
                if(playerLife > 0)
                {

                    switch (playerLife){
                        case 3:
                            DisplayHangman(graph1,width,height);
                            break;

                        case 2:
                            DisplayHangman(graph2,width,height);
                            break;

                        case 1:
                            DisplayHangman(graph3,width,height);
                            break;
                    }

                    System.out.println("Guess the word below");
                    System.out.println(hiddenKeyword);

                    String playerGuess = readKey.nextLine();
                    // get first letter only.
                    char letter = playerGuess.charAt(0);
                    boolean alreadyGuessed = false;

                    for (int i = 0; i < previousGuesses.size(); i++)
                    {
                        if(previousGuesses.get(i) == letter)
                        {
                            alreadyGuessed = true;
                        }
                    }

                    if(!alreadyGuessed)
                    {
                        previousGuesses.add(letter);

                        ArrayList result = SearchKeyword(letter,keyword);
                        correctAwnsers += result.size();

                        // Let player guess
                        if(result.size() > 0)
                        {
                            hiddenKeyword = DemystifyKeyword(result,keyword,hiddenKeyword);
                        }
                        else
                        {
                            playerLife--;
                            System.out.println("Wrong guess - you loose a life " + playerLife + "/3");
                            // Player loses one life
                        }

                        result.clear();
                    }
                    else
                    {
                        System.out.println("You already guessed " + letter + " please use another letter.");
                    }

                    // Winstate
                    if(correctAwnsers >= keyword.length())
                    {

                        switch (playerLife){
                            case 3:
                                DisplayHangman(graph1,width,height);
                                break;

                            case 2:
                                DisplayHangman(graph2,width,height);
                                break;

                            case 1:
                                DisplayHangman(graph3,width,height);
                                break;
                        }
                        System.out.println(keyword);

                        keyword = hiddenKeyword =  "";
                        playerLife = 3;
                        correctAwnsers = 0;

                        System.out.println("You win !");

                        System.out.println("Play again ? Y/N");

                        String inputWin = readKey.nextLine();
                        inputWin.toLowerCase();
                        char letterWin = inputWin.charAt(0);

                        if(letterWin == 'n')
                        {
                            isGame = false;
                        }
                    }
                }
                else
                {
                    // Show hangman
                    DisplayHangman(graph4,width,height);
                    System.out.println("He died...");
                    System.out.println("The word was " + keyword);

                    keyword = hiddenKeyword =  "";
                    playerLife = 3;
                    correctAwnsers = 0;

                    System.out.println("Try again ? Y/N");

                    String input = readKey.nextLine();
                    input.toLowerCase();
                    char letter = input.charAt(0);

                    if(letter != 'y')
                    {
                        isGame = false;
                    }
                }
            }
            else
            {
                // Create a keycode and obsucate it.
                keyword = getRandomWordIndex(guessWords);
                hiddenKeyword = getObtusedString(keyword);

                previousGuesses.clear();
                // cheats
                //System.out.println("Test ob -> " + keyword);
                //System.out.println("Test ob -> " + hiddenKeyword);
            }
        }
    }

    public static String getRandomWordIndex(String[] wordPool)
    {
        Random rand = new Random();
        int randNum = rand.nextInt(wordPool.length);
        return wordPool[randNum];
    }

    public static String getObtusedString(String word)
    {
        char[] array = new char[word.length()];
        int pos = 0;
        while (pos < word.length()) {
            array[pos] = '*';
            pos++;
        }
        return new String(array);
    }

    // guess state is array because there could be multiple letters
    // of same type. If list returned is empty - we dident guess any letter.
    public static  ArrayList<Integer> SearchKeyword(char guess,String keyword)
    {
        // will grow dynamically.
        ArrayList<Integer> guessState = new ArrayList<>();

        char[] searchWord = keyword.toCharArray();

        char upperCase = Character.toUpperCase(guess);
        char lowerCase = Character.toLowerCase(guess);

        for (int i = 0; i < keyword.length(); i++)
        {
            if(searchWord[i] == upperCase | searchWord[i] == lowerCase)
            {
                guessState.add(i);
            }
        }

        return guessState;
    }

    public static String DemystifyKeyword(ArrayList<Integer> toUnHide,String keyword, String obtuseKeyword)
    {
        StringBuilder newString = new StringBuilder(obtuseKeyword);
        char[] unhiddenWord = keyword.toCharArray();

        for (int i = 0; i < toUnHide.size(); i++)
        {
            newString.setCharAt(toUnHide.get(i),keyword.charAt(toUnHide.get(i)));
        }

        return newString.toString();
    }

    public static void DisplayHangman(char[] _hangmanGraph,int width, int height)
    {

        int vertical = 0;
        int horizontial = 0;

        // 3/3 == all asterisk besides the noose.
        // 2/3 == main trunk.
        // 1/3 == legs
        // 0/3 == arms

        for (int y = 0; y < height; y++)
        {

            for (int x = 0; x < width; x++)
            {

                System.out.print(_hangmanGraph[horizontial]);
                horizontial += 1;
            }

            System.out.println();
            vertical += 1;
        }
    }
}

