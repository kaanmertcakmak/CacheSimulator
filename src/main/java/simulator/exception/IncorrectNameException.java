//
//                               ASSIA, Inc.
//                     Confidential and Proprietary
//                         ALL RIGHTS RESERVED.
//
//      This software is provided under license and may be used
//      or distributed only in accordance with the terms of
//      such license.
//
package simulator.exception;

/**
 * @since 5/30/2021
 */
public class IncorrectNameException extends RuntimeException{

    public IncorrectNameException(String name, String options, String actual) {
        super(name + " name is incorrect can be one of following " + options + " but it is " + actual);
    }
}
