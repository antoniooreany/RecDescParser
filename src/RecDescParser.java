import javax.swing.JOptionPane;

public class RecDescParser {

    //---------------------------------------------------------------------------
    // Recursive descend parser for following grammar:
    //  S -> 	AS  	|  d
    //  A ->	Bc   	|  aA
    //  B -> 	epsilon |  b
    //---------------------------------------------------------------------------
    //  Example strings: d, aacd, aabcd


    private String input;   //string to be analyzed
    private int pos;      	//current position

    public RecDescParser(String string) {
        input = string;
        pos = 0;
    }


    /** Excpetion class for syntax errors */
    public class SyntaxError extends Exception {
        public SyntaxError(int pos, char found, String description) {
            super("Position " + pos + ", found '" + found + "', " + description);
        }
    };

    /**
     *  Return next character of input, or '\0' at end of input.
     */
    private char lookahead() {
        if (pos < input.length()) {
            return input.charAt(pos);
        } else {
            return '\0';
        }
    }

    /**
     * Compares the current input character with character ch.
     * If both match, the input position is advanced.
     * If not, a syntax error is signaled
     */
    private void scan(char ch) throws SyntaxError {
        char next = lookahead();
        if (ch == next)
            pos++;
        else {
            throw new SyntaxError(pos, next, "expected: " + ch);

        }
    }


    /**
     * Analysis function for additionaly introduced nonterminal S0
     * with production S0 -> S'\0'.
     * Terminal symbol '\0' is used to detect end of input.
     * S0 is start symbol of the grammar.
     */
    public void S0()  throws SyntaxError {
        S(); scan('\0');
    }




    /**
     * Analysis function for nonterminal S
     *    productions:  S -> 	AS  |  d
     *    First(AS) = {a,b,c}
     *    First(d) = {d}
     */

    private void S()  throws SyntaxError {
        char next = lookahead();
        switch(next) {
            case 'a':
            case 'b':
            case 'c':     // S->AS
                A();S();
                break;
            case 'd':     // S->d
                scan('d');
                break;
            default:
                throw new SyntaxError(pos, next, "expected: a,b,c,d" );
        }
    }

    /**
     * Analysis function for nonterminal A
     *    Productions:  A ->	Bc   	|  aA
     *    First(Bc) = {b,c}
     *    First(aA) = {a}
     */
    private void A()  throws SyntaxError {
        char next = lookahead();
        switch(next) {
            case 'a':       // A->aA
                scan('a'); A();
                break;
            case 'b':
            case 'c':       // A->Bc
                B();scan('c');
                break;
            default:
                throw new SyntaxError(pos, next, "expected: a, b, c");
        }
    }


    /**
     * Analysis Function for nonterminal A
     *    Productions:  B -> epsilon |  b
     *    First(b) = {b}
     *    Follow(B) = {c}
     */
    private void B()   throws SyntaxError{
        char next = lookahead();
        switch(next) {
            case 'b':       //B -> b
                scan('b');
                break;
            case 'c':      // B -> epsilon
                //nothing to do
                break;
            default:
                throw new SyntaxError(pos, next, "expected: b, c");
        }
    }



    //---------------------------------------------------------------------------
    // Read input strings and analyze them.
    //---------------------------------------------------------------------------
    public static void main(String[] args)
    {
        while (true) {
            String input = JOptionPane.showInputDialog("Input string:");
            if (input == null)
                return;

            // Analyse input
            try {
                RecDescParser parser = new RecDescParser(input);
                parser.S0();
                JOptionPane.showMessageDialog(null, "'" + input + "' accepted!");
            }
            catch (SyntaxError e) {
                JOptionPane.showMessageDialog(null, "Syntax error in '"+ input +"': " + e.getMessage());

            }
        }
    }



}