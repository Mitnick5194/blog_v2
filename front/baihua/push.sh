# !/bin/bash

#  提交到github集成命令
# 
#  接受两个参数，参数间使用空格分开，每个参数不能有空格（空格前后是两个参数）
#  第一个参数是提交理由，不能为空
#  第二个参数是托管github仓库地址，如果为空，则作普通的提交处理（非第一次，已经绑定过远程github地址了）
#
#  author: niezhenjie
#
#

funNativeCommit(){
	git add . 
	git commit -m $note
}

note=$1
address=$2
if test -z $note;
then
	echo 'fail log is empty'
	echo '[usage]: ./bindGithub log [github repository address]'
	exit 2
fi

#第一个参数不为空，第二个参数为空，作普通提交处理
if test -z $address
then
	funNativeCommit
	echo 'commiting...'
	git push
	echo 'done'
	exit 0
fi
#首次提交处理
git init
funNativeCommit
git remote add origin $address
echo 'commiting...'
git push -u origin master
echo 'done'

