#!/usr/bin/env bash


if test -f /var/run/secrets/nais.io/apigw/pdl-api;
then
    export  PDL_API_APIKEY=$(cat /var/run/secrets/nais.io/apigw/pdl-api)
fi
