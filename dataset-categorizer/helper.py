import os
import subprocess


def execute_cmd(path, cmd):
    """Execute windows command in input path directory"""

    working_dir = os.getcwd()
    os.chdir(path)

    out = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE,
                           stderr=subprocess.STDOUT)

    stdout, stderr = out.communicate()
    # utf-8 encoding is reverse compatible with ASCII
    str_stdout = stdout.decode("utf-8")

    os.chdir(working_dir)

    if "ERR" in str_stdout or "err" in str_stdout:
        return [False, str_stdout]
    else:
        return [True, str_stdout]


def init_log(log_file):
    """Initialize log files"""
    logger = open(log_file, "w", encoding="utf-8")
    logger.close()


def log(log_file, message):
    """Log `message` in `log_file`"""
    logger = open(log_file, "a", encoding="utf-8")
    logger.write("[Output] " + message + "\n")
    logger.close()


def error(log_file, message):
    """Log error `message` in `log_file`"""
    logger = open(log_file, "a", encoding="utf-8")
    logger.write("[Error] " + message + "\n")
    logger.close()
