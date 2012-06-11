#!/bin/sh -e

# first import test-ldap-users.ldif in ldap (for example using phpLDAPadmin)
# you must also configure smsuapi database (not automated yet)

test_dir=`pwd`/`dirname $0`
smsu_dir=$test_dir/../..
smsuapi_dir=$smsu_dir/../esup-smsuapi
esup_commons_dir=$smsu_dir/../esup-commons
tomcat_smsu_port=8080 # sed -n s/tomcat.port=//p esup-smsu/build.properties
tomcat_smsuapi_port=8081 # sed -n s/tomcat.port=//p esup-smsuapi/build.properties

download_selenium() {
    mkdir $test_dir/selenium
    cd $test_dir/selenium

    wget http://selenium.googlecode.com/files/selenium-server-standalone-2.19.0.jar
    ln -s selenium-server-standalone-*.jar selenium-server-standalone.jar

    wget http://selenium.googlecode.com/files/selenium-java-2.19.0.zip
    jar xf selenium-java*.zip 
    rm selenium-java*.zip
    rm -f selenium*/*-srcs.jar
    ln -s selenium*/selenium-java*.jar selenium-java.jar 
    ln -s selenium*/libs selenium-java-libs
}

start_selenium_server() {
    cd $test_dir
    if [ -e selenium/.server-pid ]; then stop_selenium_server; sleep 1; fi
    echo "starting selenium server"
    java -jar selenium/selenium-server-standalone.jar -browserSessionReuse -singleWindow -timeout 600 > selenium/server.output 2>&1 &
    echo $! > selenium/.server-pid
    sleep 1
}

raw_stop_selenium_server() {
    kill `cat selenium/.server-pid` 2>/dev/null
}
stop_selenium_server() {
    cd $test_dir
    echo "stopping selenium server"
    raw_stop_selenium_server ||:
    rm -f selenium/.server-pid
}

init_db() {
    cd $smsu_dir
    echo 'delete from sms; delete from blacklist;' | mysql -uuportal -pdfgdfg smsuapi

    mysqladmin -uuportal -pdfgdfg -f drop smsu ||:
    mysqladmin -uuportal -pdfgdfg create smsu
    ant -f build-devel.xml init-data
}

get_pid_listening_on_port() {
    pid=`lsof -t -sTCP:LISTEN -iTCP:$1 ||:`
}

stop_tomcat() {
    tomcat_port=$1
    timeout=60
    for c in `seq 1 $timeout`; do
	get_pid_listening_on_port $tomcat_port
	if [ -z "$pid" ]; then
	    return
	fi

	echo "stopping tomcat (pid:$pid)"
	kill "$pid"
	sleep 1
    done

    get_pid_listening_on_port $tomcat_port
    echo "stopping tomcat hard (pid:$pid)"
    kill -9 "$pid"
}

wait_tomcat_started() {
    tomcat_port=$1
    timeout=60
    for c in `seq 1 $timeout`; do
	sleep 1
	get_pid_listening_on_port $tomcat_port
	if [ -n "$pid" ]; then 
	    sleep 4
	    echo "tomcat has started on port $tomcat_port (pid:$pid)"
	    return
	fi
    done
    echo "timeout waiting for tomcat to start on port $tomcat_port"
    false
}

stop_smsu() {
    stop_tomcat $tomcat_smsu_port
}

stop_smsuapi() {
    stop_tomcat $tomcat_smsuapi_port
}

start_smsu() {
    stop_smsu
    cd $smsu_dir
    ant -f build-devel.xml start &
    wait_tomcat_started $tomcat_smsu_port
}

start_smsuapi() {
    stop_smsuapi
    cd $smsuapi_dir
    ant -f build-devel.xml start &
    wait_tomcat_started $tomcat_smsuapi_port
}

launch_test() {
    cd $test_dir
    junit_jar=$esup_commons_dir/webapp/WEB-INF/lib/junit-3.8.2.jar
    libs=`echo selenium/selenium-java-libs/*.jar | sed 's/ /:/g'`
    selenium_junit_classpath=".:selenium/selenium-java.jar:$junit_jar:$libs"
    javac -classpath "$selenium_junit_classpath" -encoding UTF-8 TestSend.java
    java -classpath "$selenium_junit_classpath" junit.textui.TestRunner TestSend
}

all() {
    if [ ! -e $test_dir/selenium ]; then
	download_selenium
    fi

    start_selenium_server
    trap 'raw_stop_selenium_server; exit' HUP INT TERM
    init_db
    start_smsuapi
    start_smsu
    launch_test
    stop_selenium_server
}

known_commands="download_selenium|start_selenium_server|stop_selenium_server|init_db|start_smsu|stop_smsu|launch_test|start_smsuapi"

usage() {
    echo "$0 [$known_commands]*"
    exit 1
}

if [ "$#" = 0 ]; then
    all
else
    while [ "$#" != 0 ]; do
	cmd="$1"
	shift
	if echo "$cmd" | egrep -q "^($known_commands)$"; then
	    $cmd
	else
	    echo "unknown command $cmd"
	    usage
	fi
    done
fi
