package org.test.router;

import org.apache.camel.builder.RouteBuilder;

public class TestRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("restlet:/user?restletMethod=POST")
                .setBody(simple("insert into user(firstName, lastName) values('${header.firstName}','${header.lastName}'); CALL IDENTITY();"))
                .to("jdbc:dataSource")
                .setBody(simple("select * from user ORDER BY id desc LIMIT 1"))
                .to("jdbc:dataSource");

        from("restlet:/user/{userId}?restletMethods=GET,PUT,DELETE")
                .choice()
                    .when(simple("${header.CamelHttpMethod} == 'GET'"))
                        .setBody(simple("select * from user where id = ${header.userId}"))
                    .when(simple("${header.CamelHttpMethod} == 'PUT'"))
                        .setBody(simple("update user set firstName='${header.firstName}', lastName='${header.lastName}' where id = ${header.userId}"))
                    .when(simple("${header.CamelHttpMethod} == 'DELETE'"))
                        .setBody(simple("delete from user where id = ${header.userId}"))
                    .otherwise()
                        .stop()
                .end()
                .to("jdbc:dataSource");

        from("restlet:/users")
                .setBody(simple("select * from user"))
                .to("jdbc:dataSource");
    }
}
