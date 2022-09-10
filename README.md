# About this repository

The code in this repository automates use of cards in ZRPG game [The Tale](https://the-tale.org).
Following actions are automated:
* trigger `resurrect` card if hero is dead;
* trigger `quest` card if hero procrastinates;

# How to

This chapter describes in details how to deploy the service for your needs.

## Prerequisites

Prepare environment and subscriptions in public services:
* [Python 3.x](https://www.python.org/downloads/)
* [Terraform](https://www.terraform.io/downloads)
* [AWS subscription](https://aws.amazon.com)

## Python

Code is written using Python 3.x and tested against version 3.9 and 3.10.
The script reads environment variable `SESSION_ID` and uses it to authenticate in the game.
To obtain value for this variable, log into the game in a browser, open development mode and find `sessionid` cookie.

In order to prepare sources for AWS Lambda, it's necessary to download dependencies, zip sources into archive and copy to Terraform directory.
Execute code below in `python` directory:
```shell
pip3 install -r requirements.txt -t .
zip -r tale-keep-alive.zip ./*
mv tale-keep-alive.zip ../terraform/
```

## Terraform

It's required to fill in 4 mandatory parameters in order to provision infrastructure:
* aws_access_key_id - access key of AWS user with programmatic access
* aws_secret_key_id - secret key of AWS user with programmatic access
* session_id - session id of authenticated user in a game
* s3_bucket_name - name of existing S3 bucket where Lambda archive will be uploaded

Author uses Terraform Cloud, you would need to change Terraform block to yours.

Run `terraform apply -auto-approve` to get infrastructure and Lambda provisioned.
Once complete, Lambda function will get triggered by Cloudwatch from time to time.

## AWS

Setup AWS user with programmatic access and following permissions:
* AmazonSSMFullAccess
* AmazonS3FullAccess
* AmazonEventBridgeFullAccess
* AWSLambda_FullAccess

AWS permissions are not hardenized.
