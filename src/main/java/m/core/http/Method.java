package m.core.http;

/**
 * HTTP Methods https://tools.ietf.org/html/rfc2616#section-5.1.1
 *
 * Including PATCH https://tools.ietf.org/html/rfc5789
 *
 * CONNECT the reserved method, not included
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public enum Method {
    OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, PATCH
}