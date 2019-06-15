package com.hackorama.mcore.common;

/**
 * Ignore invalid FindBugs warning without library dependency
 *
 * Refer : https://sourceforge.net/p/findbugs/feature-requests/298/
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public @interface SuppressFBWarnings {
    String[] value() default { "UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS" };

    String justification() default "UMAC does not consider access through Function interface";
}
