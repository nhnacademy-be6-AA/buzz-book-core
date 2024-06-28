package store.buzzbook.core.common.util;

import java.util.List;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;

public class FunctionUtil {

    public static void orderDescFilter(List<OrderSpecifier> orderBy, Boolean value, Path<?> parent, String property) {
        if (value)
            orderBy.add(new OrderSpecifier(Order.DESC, Expressions.path(Object.class, parent, property)));
        else
            orderBy.add(new OrderSpecifier(Order.ASC, Expressions.path(Object.class, parent, property)));
    }

    public static void orderDescVariableFilter(List<OrderSpecifier> orderBy, Boolean value, String variable){
        if (value)
            orderBy.add(new OrderSpecifier(Order.DESC, Expressions.stringPath(variable)));
        else
            orderBy.add(new OrderSpecifier(Order.ASC, Expressions.stringPath(variable)));
    }

}
