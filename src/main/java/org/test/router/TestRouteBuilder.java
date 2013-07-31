/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.test.router;

import org.apache.camel.builder.RouteBuilder;

public class TestRouteBuilder extends RouteBuilder {

    @Override
    public void configure() {
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
