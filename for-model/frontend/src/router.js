

forEach: Model
fileName: router.js

path: frontend/src
---

import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);


{{#boundedContexts}}
    {{#aggregates}}
import {{namePascalCase}}Manager from "./components/listers/{{namePascalCase}}Cards"
import {{namePascalCase}}Detail from "./components/listers/{{namePascalCase}}Detail"
    {{/aggregates}}

    {{#viewes}}
        {{#ifEquals dataProjection "cqrs"}}
import {{namePascalCase}}View from "./components/{{namePascalCase}}View"
import {{namePascalCase}}ViewDetail from "./components/{{namePascalCase}}ViewDetail"
        {{/ifEquals}}
    {{/viewes}}
{{/boundedContexts}}

export default new Router({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: [
       {{#boundedContexts}}
        {{#aggregates}}
            {
                path: '/{{namePlural}}',
                name: '{{namePascalCase}}Manager',
                component: {{namePascalCase}}Manager
            },
            {
                path: '/{{namePlural}}/:id',
                name: '{{namePascalCase}}Detail',
                component: {{namePascalCase}}Detail
            },
        {{/aggregates}}

        {{#viewes}}
        {{#ifEquals dataProjection "cqrs"}}

            {
                path: '/{{namePlural}}',
                name: '{{namePascalCase}}View',
                component: {{namePascalCase}}View
            },
            {
                path: '/{{namePlural}}/:id',
                name: '{{namePascalCase}}ViewDetail',
                component: {{namePascalCase}}ViewDetail
            },
        {{/ifEquals}}
        {{/viewes}}
       {{/boundedContexts}}


    ]
})
