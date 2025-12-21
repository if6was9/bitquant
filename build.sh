#!/bin/bash

set -e 


if [[ "${CI}" = "true" ]]; then
env
    cat <<EOF >./config.yml
MASSIVE_API_KEY: ${MASSIVE_API_KEY}
EOF
fi

./mvnw -B clean test 


