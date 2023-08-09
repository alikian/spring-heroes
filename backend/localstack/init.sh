aws secretsmanager delete-secret \
    --secret-id /secrets/HeroesSecrets \
    --force-delete-without-recovery \
    --endpoint-url http://localhost:4566/
aws secretsmanager create-secret \
    --name /secrets/HeroesSecrets \
    --secret-string file://secrets.json \
    --endpoint-url http://localhost:4566/