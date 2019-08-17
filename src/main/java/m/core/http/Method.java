package m.core.http;

/**
 * HTTP Methods.
 * <p>
 * As defined in RFC2616 and including {@code PATCH} from RFC5789 and excluding the reserved {@code CONNECT}.
 *
 * @see <a href="https://tools.ietf.org/html/rfc2616#section-5.1.1">RFC2616 5.1.1</a>
 * @see <a href="https://tools.ietf.org/html/rfc5789">RFC5789</a>
 */
public enum Method {
    OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, PATCH
}
