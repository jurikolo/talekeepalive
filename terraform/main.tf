terraform {
  cloud {
    organization = "jurikolo"

    workspaces {
      name = "talekeepalive"
    }
  }

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 4.29.0"
    }
  }
}

provider "aws" {
  region     = "eu-central-1"
  access_key = var.aws_access_key_id
  secret_key = var.aws_secret_key_id
}

locals {
  tags = {
    Name       = var.project_name
    CostCenter = var.project_name
    CreatedBy  = "Terraform"
  }
}

resource "aws_s3_object" "lambda" {
  bucket = var.s3_bucket_name
  key    = "${var.function_name}.zip"
  source = "${path.module}/${var.function_name}.zip"
  etag   = filemd5("${path.module}/${var.function_name}.zip")
  tags   = local.tags

}

resource "aws_lambda_function" "lambda" {
  function_name    = var.function_name
  s3_bucket        = var.s3_bucket_name
  s3_key           = aws_s3_object.lambda.key
  role             = aws_iam_role.lambda.arn
  runtime          = "python3.9"
  handler          = "main.handler"
  source_code_hash = filebase64sha256("${var.function_name}.zip")
  environment {
    variables = {
      SESSION_ID = var.session_id
    }
  }
  tags = local.tags
}

resource "aws_cloudwatch_event_rule" "lambda-trigger" {
  name                = var.project_name
  description         = "cron-based lambda trigger"
  schedule_expression = "cron(*/5 * * * ? *)"
  tags                = local.tags
}

resource "aws_cloudwatch_event_target" "lambda-trigger" {
  rule = aws_cloudwatch_event_rule.lambda-trigger.name
  arn  = aws_lambda_function.lambda.arn
}

resource "aws_lambda_permission" "lambda-trigger" {
  function_name = aws_lambda_function.lambda.function_name
  principal     = "events.amazonaws.com"
  action        = "lambda:InvokeFunction"
  source_arn    = aws_cloudwatch_event_rule.lambda-trigger.arn
}

resource "aws_iam_role" "lambda" {
  name               = var.project_name
  assume_role_policy = jsonencode({
    Version   = "2012-10-17"
    Statement = [
      {
        Action    = "sts:AssumeRole"
        Effect    = "Allow"
        Sid       = ""
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      }
    ]
  })

  managed_policy_arns = ["arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"]
  tags = local.tags
}

resource "aws_cloudwatch_log_group" "function_log_group" {
  name              = "/aws/lambda/${aws_lambda_function.lambda.function_name}"
  retention_in_days = 7
  lifecycle {
    prevent_destroy = false
  }
}
