variable "aws_access_key_id" {
  type = string
}

variable "aws_secret_key_id" {
  type = string
}

variable "session_id" {
  type        = string
  description = "Session ID of authorized user from browser"
}

variable "s3_bucket_name" {
  type        = string
  description = "Name of existing S3 bucket to upload Lambda function"
}

variable "function_name" {
  type        = string
  description = "Name of Lambda function"
  default     = "tale-keep-alive"
}

variable "project_name" {
  type        = string
  description = "Project name to name the resources"
  default     = "talekeepalive"
}