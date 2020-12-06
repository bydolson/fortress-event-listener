package fortress.eventlistener.converter;

import fortress.eventconsumer.model.Name;

public interface NameConverter<T> {

    Name convert(T input);
}
