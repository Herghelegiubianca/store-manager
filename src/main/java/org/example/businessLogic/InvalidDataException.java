package org.example.businessLogic;

/**
 * Exceptie aruncata cand datele primite sunt invalide.
 *
 * @author Bianca Herghelegiu
 */
public class InvalidDataException extends Exception{
    public InvalidDataException(String msg)
    {
        super(msg);
    }
}
