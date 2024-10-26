package com.mobilitus.attractionscmd.oiw;

import com.mobilitus.attractionscmd.oiw.data.EventList;
import com.mobilitus.util.data.attractions.DataSource;
import com.mobilitus.util.data.aws.kinesis.KinesisStream;
import com.mobilitus.util.data.pusher.MessageType;
import com.mobilitus.util.data.pusher.PusherMessage;
import com.mobilitus.util.data.schema.SchemaEvent;
import com.mobilitus.util.data.utils.ActivityStream.internal.v2.PropertyType;
import com.mobilitus.util.distributed.aws.kinesis.Producer;
import com.mobilitus.util.distributed.dynamodb.AWSUtils;
import com.mobilitus.util.hexia.Pair;
import com.mobilitus.util.hexia.StrUtil;
import com.mobilitus.util.httputil.HTTPUtil;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * @author helgaw
 * @since 9/23/24 17:14
 */
public class OIWScraper
{

    private static final Logger logger = Logger.getLogger(OIWScraper.class);
    private final Producer toEventCreator;

    public OIWScraper()
    {
        toEventCreator = new Producer(KinesisStream.toSchema, AWSUtils.getCredentialsProvider());

    }

    public void scrapeOIW()
    {
        String url = "https://h1jmcyiv.apicdn.sanity.io/v2021-08-31/data/query/production?query=*%5B_type+%3D%3D+%27event%27%5D%7B+_id%2C+title%2C+slug%2C+startDate%2C+endDate%2C+externalUrl%2Cingress%2Cvenue-%3E%7B...%7D%2C+speakers%5B%5D-%3E%7B...%7D%2C+hosts%5B%5D-%3E%7B...%7D%2Cformat%5B%5D-%3E%7B...%7D%2C+labels%5B%5D-%3E%7B...%7D%7D";
        Pair<Integer, String> jsonPair  = HTTPUtil.getJson(url, 30000);
        if (jsonPair.getKey() == 200)
        {
//            System.out.println(js/Users/helgaw/Library/Java/JavaVirtualMachines/openjdk-21.0.2/Contents/Home/bin/java -agentlib:jdwp=transport=dt_socket,address=127.0.0.1:52083,suspend=y,server=n -ea -javaagent:/Users/helgaw/.m2/repository/org/jetbrains/kotlinx/kotlinx-coroutines-core-jvm/1.5.2/kotlinx-coroutines-core-jvm-1.5.2.jar -Didea.test.cyclic.buffer.size=1048576 -javaagent:/Users/helgaw/Library/Caches/JetBrains/IntelliJIdea2024.2/captureAgent/debugger-agent.jar=file:/private/var/folders/lf/n04l2nq56qj42y_g00s3ts100000gp/T/capture.props -Dkotlinx.coroutines.debug.enable.creation.stack.trace=false -Ddebugger.agent.enable.coroutines=true -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath /Users/helgaw/.m2/repository/org/junit/platform/junit-platform-launcher/1.11.0/junit-platform-launcher-1.11.0.jar:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar:/Applications/IntelliJ IDEA.app/Contents/plugins/junit/lib/junit5-rt.jar:/Applications/IntelliJ IDEA.app/Contents/plugins/junit/lib/junit-rt.jar:/Users/Shared/projects/code/AttractionsCMD/target/test-classes:/Users/Shared/projects/code/AttractionsCMD/target/classes:/Users/helgaw/.m2/repository/com/mobilitus/hexiaUtils/1.0.5-SNAPSHOT/hexiautils-1.0.5-20240813.163024-2.jar:/Users/helgaw/.m2/repository/com/mobilitus/httpUtils/1.0-SNAPSHOT/httpUtils-1.0-20240805.121024-1.jar:/Users/helgaw/.m2/repository/com/mobilitus/dataUtils/1.0.28-SNAPSHOT/datautils-1.0.28-SNAPSHOT.jar:/Users/helgaw/.m2/repository/org/jsoup/jsoup/1.18.1/jsoup-1.18.1.jar:/Users/helgaw/.m2/repository/com/mobilitus/distributedServices/1.2-SNAPSHOT/distributedservices-1.2-20240923.172746-1.jar:/Users/helgaw/.m2/repository/com/mobilitus/cacheutil/1.1.1-SNAPSHOT/cacheutil-1.1.1-20240923.172424-1.jar:/Users/helgaw/.m2/repository/com/googlecode/xmemcached/xmemcached/2.4.8/xmemcached-2.4.8.jar:/Users/helgaw/.m2/repository/org/apache/httpcomponents/httpcore/4.4.16/httpcore-4.4.16.jar:/Users/helgaw/.m2/repository/org/apache/commons/commons-imaging/1.0.0-alpha5/commons-imaging-1.0.0-alpha5.jar:/Users/helgaw/.m2/repository/org/apache/httpcomponents/client5/httpclient5/5.4/httpclient5-5.4.jar:/Users/helgaw/.m2/repository/org/apache/httpcomponents/core5/httpcore5/5.3/httpcore5-5.3.jar:/Users/helgaw/.m2/repository/org/apache/httpcomponents/core5/httpcore5-h2/5.3/httpcore5-h2-5.3.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/cloudsearchdomain/2.28.6/cloudsearchdomain-2.28.6.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/rekognition/2.28.6/rekognition-2.28.6.jar:/Users/helgaw/.m2/repository/com/rabbitmq/amqp-client/5.22.0/amqp-client-5.22.0.jar:/Users/helgaw/.m2/repository/commons-logging/commons-logging/1.3.4/commons-logging-1.3.4.jar:/Users/helgaw/.m2/repository/com/mobilitus/persistentData/1.6.23-SNAPSHOT/persistentdata-1.6.23-SNAPSHOT.jar:/Users/helgaw/.m2/repository/com/mobilitus/promoDataUtils/1.0.43-SNAPSHOT/promodatautils-1.0.43-SNAPSHOT.jar:/Users/helgaw/.m2/repository/joda-time/joda-time/2.13.0/joda-time-2.13.0.jar:/Users/helgaw/.m2/repository/com/google/code/gson/gson/2.11.0/gson-2.11.0.jar:/Users/helgaw/.m2/repository/com/google/errorprone/error_prone_annotations/2.27.0/error_prone_annotations-2.27.0.jar:/Users/helgaw/.m2/repository/org/apache/commons/commons-text/1.12.0/commons-text-1.12.0.jar:/Users/helgaw/.m2/repository/org/apache/commons/commons-lang3/3.14.0/commons-lang3-3.14.0.jar:/Users/helgaw/.m2/repository/org/apache/httpcomponents/httpclient-cache/4.5.14/httpclient-cache-4.5.14.jar:/Users/helgaw/.m2/repository/org/apache/httpcomponents/httpclient/4.5.14/httpclient-4.5.14.jar:/Users/helgaw/.m2/repository/commons-codec/commons-codec/1.11/commons-codec-1.11.jar:/Users/helgaw/.m2/repository/commons-io/commons-io/2.17.0/commons-io-2.17.0.jar:/Users/helgaw/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.18.0-rc1/jackson-databind-2.18.0-rc1.jar:/Users/helgaw/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.18.0-rc1/jackson-annotations-2.18.0-rc1.jar:/Users/helgaw/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.18.0-rc1/jackson-core-2.18.0-rc1.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/dynamodb/2.28.7/dynamodb-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/aws-json-protocol/2.28.7/aws-json-protocol-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/third-party-jackson-core/2.28.7/third-party-jackson-core-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/protocol-core/2.28.7/protocol-core-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/profiles/2.28.7/profiles-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/retries-spi/2.28.7/retries-spi-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/http-auth-aws/2.28.7/http-auth-aws-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/sdk-core/2.28.7/sdk-core-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/retries/2.28.7/retries-2.28.7.jar:/Users/helgaw/.m2/repository/org/reactivestreams/reactive-streams/1.0.4/reactive-streams-1.0.4.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/http-auth-spi/2.28.7/http-auth-spi-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/http-auth/2.28.7/http-auth-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/identity-spi/2.28.7/identity-spi-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/http-client-spi/2.28.7/http-client-spi-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/regions/2.28.7/regions-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/annotations/2.28.7/annotations-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/utils/2.28.7/utils-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/aws-core/2.28.7/aws-core-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/metrics-spi/2.28.7/metrics-spi-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/json-utils/2.28.7/json-utils-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/endpoints-spi/2.28.7/endpoints-spi-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/apache-client/2.28.7/apache-client-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/netty-nio-client/2.28.7/netty-nio-client-2.28.7.jar:/Users/helgaw/.m2/repository/io/netty/netty-codec-http/4.1.112.Final/netty-codec-http-4.1.112.Final.jar:/Users/helgaw/.m2/repository/io/netty/netty-codec-http2/4.1.112.Final/netty-codec-http2-4.1.112.Final.jar:/Users/helgaw/.m2/repository/io/netty/netty-codec/4.1.112.Final/netty-codec-4.1.112.Final.jar:/Users/helgaw/.m2/repository/io/netty/netty-transport/4.1.112.Final/netty-transport-4.1.112.Final.jar:/Users/helgaw/.m2/repository/io/netty/netty-common/4.1.112.Final/netty-common-4.1.112.Final.jar:/Users/helgaw/.m2/repository/io/netty/netty-buffer/4.1.112.Final/netty-buffer-4.1.112.Final.jar:/Users/helgaw/.m2/repository/io/netty/netty-handler/4.1.112.Final/netty-handler-4.1.112.Final.jar:/Users/helgaw/.m2/repository/io/netty/netty-transport-native-unix-common/4.1.112.Final/netty-transport-native-unix-common-4.1.112.Final.jar:/Users/helgaw/.m2/repository/io/netty/netty-transport-classes-epoll/4.1.112.Final/netty-transport-classes-epoll-4.1.112.Final.jar:/Users/helgaw/.m2/repository/io/netty/netty-resolver/4.1.112.Final/netty-resolver-4.1.112.Final.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/auth/2.28.7/auth-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/http-auth-aws-eventstream/2.28.7/http-auth-aws-eventstream-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/eventstream/eventstream/1.0.1/eventstream-1.0.1.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/dynamodb-enhanced/2.28.7/dynamodb-enhanced-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/s3/2.28.7/s3-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/aws-xml-protocol/2.28.7/aws-xml-protocol-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/aws-query-protocol/2.28.7/aws-query-protocol-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/arns/2.28.7/arns-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/crt-core/2.28.7/crt-core-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/checksums/2.28.7/checksums-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/checksums-spi/2.28.7/checksums-spi-2.28.7.jar:/Users/helgaw/.m2/repository/software/amazon/kinesis/amazon-kinesis-client/2.6.0/amazon-kinesis-client-2.6.0.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/kinesis/2.25.11/kinesis-2.25.11.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/aws-cbor-protocol/2.25.11/aws-cbor-protocol-2.25.11.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/third-party-jackson-dataformat-cbor/2.25.11/third-party-jackson-dataformat-cbor-2.25.11.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/cloudwatch/2.25.11/cloudwatch-2.25.11.jar:/Users/helgaw/.m2/repository/software/amazon/glue/schema-registry-serde/1.1.19/schema-registry-serde-1.1.19.jar:/Users/helgaw/.m2/repository/com/amazonaws/aws-java-sdk-sts/1.12.660/aws-java-sdk-sts-1.12.660.jar:/Users/helgaw/.m2/repository/com/amazonaws/aws-java-sdk-core/1.12.660/aws-java-sdk-core-1.12.660.jar:/Users/helgaw/.m2/repository/com/fasterxml/jackson/dataformat/jackson-dataformat-cbor/2.12.6/jackson-dataformat-cbor-2.12.6.jar:/Users/helgaw/.m2/repository/com/amazonaws/jmespath-java/1.12.660/jmespath-java-1.12.660.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/sts/2.22.12/sts-2.22.12.jar:/Users/helgaw/.m2/repository/org/apache/kafka/kafka-clients/3.6.1/kafka-clients-3.6.1.jar:/Users/helgaw/.m2/repository/com/github/luben/zstd-jni/1.5.5-1/zstd-jni-1.5.5-1.jar:/Users/helgaw/.m2/repository/org/lz4/lz4-java/1.8.0/lz4-java-1.8.0.jar:/Users/helgaw/.m2/repository/org/xerial/snappy/snappy-java/1.1.10.5/snappy-java-1.1.10.5.jar:/Users/helgaw/.m2/repository/com/kjetland/mbknor-jackson-jsonschema_2.12/1.0.39/mbknor-jackson-jsonschema_2.12-1.0.39.jar:/Users/helgaw/.m2/repository/org/scala-lang/scala-library/2.12.10/scala-library-2.12.10.jar:/Users/helgaw/.m2/repository/javax/validation/validation-api/2.0.1.Final/validation-api-2.0.1.Final.jar:/Users/helgaw/.m2/repository/io/github/classgraph/classgraph/4.8.120/classgraph-4.8.120.jar:/Users/helgaw/.m2/repository/com/github/erosb/everit-json-schema/1.14.2/everit-json-schema-1.14.2.jar:/Users/helgaw/.m2/repository/commons-validator/commons-validator/1.7/commons-validator-1.7.jar:/Users/helgaw/.m2/repository/commons-digester/commons-digester/2.1/commons-digester-2.1.jar:/Users/helgaw/.m2/repository/com/damnhandy/handy-uri-templates/2.1.8/handy-uri-templates-2.1.8.jar:/Users/helgaw/.m2/repository/com/google/re2j/re2j/1.6/re2j-1.6.jar:/Users/helgaw/.m2/repository/org/jetbrains/kotlin/kotlin-stdlib/1.7.10/kotlin-stdlib-1.7.10.jar:/Users/helgaw/.m2/repository/org/jetbrains/kotlin/kotlin-stdlib-common/1.7.10/kotlin-stdlib-common-1.7.10.jar:/Users/helgaw/.m2/repository/org/jetbrains/annotations/13.0/annotations-13.0.jar:/Users/helgaw/.m2/repository/org/jetbrains/kotlin/kotlin-stdlib-jdk8/1.7.10/kotlin-stdlib-jdk8-1.7.10.jar:/Users/helgaw/.m2/repository/org/jetbrains/kotlin/kotlin-stdlib-jdk7/1.7.10/kotlin-stdlib-jdk7-1.7.10.jar:/Users/helgaw/.m2/repository/org/jetbrains/kotlin/kotlin-reflect/1.7.10/kotlin-reflect-1.7.10.jar:/Users/helgaw/.m2/repository/org/jetbrains/kotlin/kotlin-scripting-compiler-impl-embeddable/1.7.10/kotlin-scripting-compiler-impl-embeddable-1.7.10.jar:/Users/helgaw/.m2/repository/org/jetbrains/kotlin/kotlin-scripting-common/1.7.10/kotlin-scripting-common-1.7.10.jar:/Users/helgaw/.m2/repository/org/jetbrains/kotlin/kotlin-scripting-jvm/1.7.10/kotlin-scripting-jvm-1.7.10.jar:/Users/helgaw/.m2/repository/org/jetbrains/kotlin/kotlin-script-runtime/1.7.10/kotlin-script-runtime-1.7.10.jar:/Users/helgaw/.m2/repository/org/jetbrains/kotlin/kotlin-scripting-compiler-embeddable/1.7.10/kotlin-scripting-compiler-embeddable-1.7.10.jar:/Users/helgaw/.m2/repository/com/squareup/okio/okio/3.4.0/okio-3.4.0.jar:/Users/helgaw/.m2/repository/com/squareup/okio/okio-jvm/3.4.0/okio-jvm-3.4.0.jar:/Users/helgaw/.m2/repository/com/squareup/okio/okio-fakefilesystem/3.2.0/okio-fakefilesystem-3.2.0.jar:/Users/helgaw/.m2/repository/com/squareup/okio/okio-fakefilesystem-jvm/3.2.0/okio-fakefilesystem-jvm-3.2.0.jar:/Users/helgaw/.m2/repository/org/jetbrains/kotlinx/kotlinx-datetime-jvm/0.3.2/kotlinx-datetime-jvm-0.3.2.jar:/Users/helgaw/.m2/repository/org/jetbrains/kotlinx/kotlinx-serialization-core-jvm/1.4.0/kotlinx-serialization-core-jvm-1.4.0.jar:/Users/helgaw/.m2/repository/com/squareup/wire/wire-schema/4.3.0/wire-schema-4.3.0.jar:/Users/helgaw/.m2/repository/com/squareup/wire/wire-runtime/4.3.0/wire-runtime-4.3.0.jar:/Users/helgaw/.m2/repository/com/squareup/wire/wire-compiler/4.3.0/wire-compiler-4.3.0.jar:/Users/helgaw/.m2/repository/com/squareup/wire/wire-schema-jvm/4.3.0/wire-schema-jvm-4.3.0.jar:/Users/helgaw/.m2/repository/com/squareup/wire/wire-runtime-jvm/4.3.0/wire-runtime-jvm-4.3.0.jar:/Users/helgaw/.m2/repository/com/squareup/wire/wire-kotlin-generator/4.3.0/wire-kotlin-generator-4.3.0.jar:/Users/helgaw/.m2/repository/com/squareup/kotlinpoet/1.10.2/kotlinpoet-1.10.2.jar:/Users/helgaw/.m2/repository/com/squareup/wire/wire-grpc-client-jvm/4.3.0/wire-grpc-client-jvm-4.3.0.jar:/Users/helgaw/.m2/repository/com/squareup/okhttp3/okhttp/4.9.3/okhttp-4.9.3.jar:/Users/helgaw/.m2/repository/org/jetbrains/kotlinx/kotlinx-coroutines-core-jvm/1.5.2/kotlinx-coroutines-core-jvm-1.5.2.jar:/Users/helgaw/.m2/repository/com/squareup/wire/wire-grpc-server-generator/4.3.0/wire-grpc-server-generator-4.3.0.jar:/Users/helgaw/.m2/repository/com/squareup/wire/wire-java-generator/4.3.0/wire-java-generator-4.3.0.jar:/Users/helgaw/.m2/repository/com/squareup/javapoet/1.13.0/javapoet-1.13.0.jar:/Users/helgaw/.m2/repository/com/squareup/wire/wire-swift-generator/4.3.0/wire-swift-generator-4.3.0.jar:/Users/helgaw/.m2/repository/io/outfoxx/swiftpoet/1.3.1/swiftpoet-1.3.1.jar:/Users/helgaw/.m2/repository/com/squareup/wire/wire-profiles/4.3.0/wire-profiles-4.3.0.jar:/Users/helgaw/.m2/repository/com/google/api/grpc/proto-google-common-protos/2.7.4/proto-google-common-protos-2.7.4.jar:/Users/helgaw/.m2/repository/com/google/jimfs/jimfs/1.1/jimfs-1.1.jar:/Users/helgaw/.m2/repository/org/apache/commons/commons-collections4/4.4/commons-collections4-4.4.jar:/Users/helgaw/.m2/repository/software/amazon/glue/schema-registry-common/1.1.19/schema-registry-common-1.1.19.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/glue/2.22.12/glue-2.22.12.jar:/Users/helgaw/.m2/repository/software/amazon/glue/schema-registry-build-tools/1.1.19/schema-registry-build-tools-1.1.19.jar:/Users/helgaw/.m2/repository/software/amazon/awssdk/url-connection-client/2.22.12/url-connection-client-2.22.12.jar:/Users/helgaw/.m2/repository/org/apache/avro/avro/1.11.3/avro-1.11.3.jar:/Users/helgaw/.m2/repository/org/apache/commons/commons-compress/1.21/commons-compress-1.21.jar:/Users/helgaw/.m2/repository/com/google/guava/guava/32.1.1-jre/guava-32.1.1-jre.jar:/Users/helgaw/.m2/repository/com/google/guava/failureaccess/1.0.1/failureaccess-1.0.1.jar:/Users/helgaw/.m2/repository/com/google/guava/listenablefuture/9999.0-empty-to-avoid-conflict-with-guava/listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar:/Users/helgaw/.m2/repository/com/google/code/findbugs/jsr305/3.0.2/jsr305-3.0.2.jar:/Users/helgaw/.m2/repository/org/checkerframework/checker-qual/3.33.0/checker-qual-3.33.0.jar:/Users/helgaw/.m2/repository/com/google/j2objc/j2objc-annotations/2.8/j2objc-annotations-2.8.jar:/Users/helgaw/.m2/repository/com/google/protobuf/protobuf-java/3.21.12/protobuf-java-3.21.12.jar:/Users/helgaw/.m2/repository/org/slf4j/slf4j-api/2.0.7/slf4j-api-2.0.7.jar:/Users/helgaw/.m2/repository/io/reactivex/rxjava3/rxjava/3.1.6/rxjava-3.1.6.jar:/Users/helgaw/.m2/repository/com/vdurmont/emoji-java/5.1.1/emoji-java-5.1.1.jar:/Users/helgaw/.m2/repository/org/json/json/20170516/json-20170516.jar:/Users/helgaw/.m2/repository/com/drewnoakes/metadata-extractor/2.19.0/metadata-extractor-2.19.0.jar:/Users/helgaw/.m2/repository/com/adobe/xmp/xmpcore/6.1.11/xmpcore-6.1.11.jar:/Users/helgaw/.m2/repository/ch/qos/reload4j/reload4j/1.2.25/reload4j-1.2.25.jar:/Users/helgaw/.m2/repository/org/junit/jupiter/junit-jupiter/5.11.0/junit-jupiter-5.11.0.jar:/Users/helgaw/.m2/repository/org/junit/jupiter/junit-jupiter-params/5.11.0/junit-jupiter-params-5.11.0.jar:/Users/helgaw/.m2/repository/org/junit/jupiter/junit-jupiter-engine/5.11.0/junit-jupiter-engine-5.11.0.jar:/Users/helgaw/.m2/repository/org/junit/platform/junit-platform-engine/1.11.0/junit-platform-engine-1.11.0.jar:/Users/helgaw/.m2/repository/org/apiguardian/apiguardian-api/1.1.2/apiguardian-api-1.1.2.jar:/Users/helgaw/.m2/repository/org/junit/jupiter/junit-jupiter-api/5.11.0/junit-jupiter-api-5.11.0.jar:/Users/helgaw/.m2/repository/org/opentest4j/opentest4j/1.3.0/opentest4j-1.3.0.jar:/Users/helgaw/.m2/repository/org/junit/platform/junit-platform-commons/1.11.0/junit-platform-commons-1.11.0.jar com.intellij.rt.junit.JUnitStarter -ideVersion5 -junit5 com.mobilitus.attractionscmd.oiw.OIWScraperTest,scrapeOIW
            //Connected to the target VM, address: '127.0.0.1:52083', transport: 'socket'
            //SLF4J: No SLF4J providers were found.
            //SLF4J: Defaulting to no-operation (NOP) logger implementation
            //SLF4J: See https://www.slf4j.org/codes.html#noProviders for further details.
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.scrapeOIW(OIWScraper.java:43)]
            //
            //
            //
            //
            //{
            //  "name": "GENERATION INNOVATION: startups of the future",
            //  "url": "https://oiw.no/event/sefio24",
            //  "startDate": "2024-09-24T15:00:00.000Z",
            //  "localStartDate": "2024-09-24T17:00",
            //  "endDate": "2024-09-24T21:30:00.000Z",
            //  "description": "Join us for an exhilarating pitch contest during Oslo Innovation Week, where students will compete by presenting their innovative ideas to a panel of experts. After the competition the event transitions into a vibrant networking and afterparty session, offering a perfect opportunity to connect, discuss, and enjoy a festive atmosphere. Don\u0027t miss this unique blend of competition and celebration—where ideas meet opportunities!",
            //  "location": {
            //    "name": "Mesh Youngstorget",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9139676,
            //      "longitude": 10.7438992
            //    },
            //    "@type": "Place",
            //    "@id": "PUn43gDAws84BhCBDZ4OsM",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "SEFiO",
            //      "url": "https://sefio.no/",
            //      "@id": "ir5Pa3JqTeBfgyynGTW50F",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/c872a58672f17a21b4b5b0cee1dc2aacc0c168ba-500x500.png",
            //      "sameAs": [
            //        "https://sefio.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://sefio.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "sefio24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Talent",
            //      "Networking",
            //      "Pitch"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] GENERATION INNOVATION: startups of the future Sending event 'GENERATION INNOVATION: startups of the future' to importer
            //{
            //  "name": "The Untapped Potentials of Grants For Startups",
            //  "url": "https://oiw.no/event/euronordic-funding-alliance",
            //  "startDate": "2024-09-25T13:00:00.000Z",
            //  "localStartDate": "2024-09-25T15:00",
            //  "endDate": "2024-09-25T15:00:00.000Z",
            //  "description": "Attracting investment for your startups has become increasingly challenging. It\u0027s time to explore alternative avenues, such as Public Funding, which holds immense potential, amounting to billions of euros.\n\nJoin us to delve into a comparison between the VC and grant processes, while we present our comprehensive support for both avenues.\n\n🌏 Connect with fellow startup enthusiasts from across Europe and Norway.\n✍️ Gain insights into a variety of grants and investments tailored to your specific needs from both grant providers and investors.\n🙌 Forge valuable partnerships for funding applications in Norway and throughout Europe.\n💪 Become a member of ENFA and tap into an international network of resources for application writing, proposal development, and fundraising.\n\nDon\u0027t miss this opportunity to leverage diverse funding opportunities and expand your startup\u0027s horizons.",
            //  "location": {
            //    "name": "NIO",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9139814,
            //      "longitude": 10.7362327
            //    },
            //    "@type": "Place",
            //    "@id": "nBW7LHeMYA8Pvl9MSoRMtK",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Euro Nordic Funding Alliance",
            //      "url": "https://www.en-fa.org/",
            //      "@id": "azBm0ox0gTIvJxtHPsqhFf",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/048588232fe7576d4d93f97818f358e30417ac55-2522x986.png",
            //      "sameAs": [
            //        "https://www.en-fa.org/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.en-fa.org/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Desinova Studios",
            //      "url": "https://www.desinovastudios.com/",
            //      "@id": "75hF6u0k3iHYTKl66OHiGQ",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/d05e78028ecd1ab38f5a408799f1cacfa568abf8-742x308.png",
            //      "sameAs": [
            //        "https://www.desinovastudios.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.desinovastudios.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Niloufar Gharavi",
            //      "url": "https://oiw.no/speaker/niloufar-gharavi",
            //      "description": "Niloufar Gharavi, known as Nilu, is a nomad entrepreneur and systemic designer, pioneering \"Design-Driven Entrepreneurship\" (DDE). She runs Desinova Studios, a Design-Driven Venture Studio, and streamlines access to grant funding for impact startups and businesses through ENFA.",
            //      "@type": "EducationGroup",
            //      "@id": "azBm0ox0gTIvJxtHPsslHq",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/edeae5a395ec966af9f17b1dcd50d0898f5187c6-828x885.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "euronordic-funding-alliance",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Investment",
            //      "Networking",
            //      "Business Conference"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] The Untapped Potentials of Grants For Startups Sending event 'The Untapped Potentials of Grants For Startups' to importer
            //{
            //  "name": "Book launch: Gamification in practise (and for the win!) | Mesh Community Stage",
            //  "url": "https://oiw.no/event/mesh-community-stage-factiverse",
            //  "startDate": "2024-09-26T13:00:00.000Z",
            //  "localStartDate": "2024-09-26T15:00",
            //  "endDate": "2024-09-26T14:30:00.000Z",
            //  "description": "",
            //  "location": {
            //    "name": "Mesh Nationaltheatret",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9130126,
            //      "longitude": 10.7341709
            //    },
            //    "@type": "Place",
            //    "@id": "FyAHGzCGYvVljtOszk0rrs",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Gaute Kokkvoll",
            //      "url": "https://www.linkedin.com/in/gautekokkvoll/",
            //      "@id": "GBgGvFlp21zYJWMNefOF9n",
            //      "sameAs": [
            //        "https://www.linkedin.com/in/gautekokkvoll/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.linkedin.com/in/gautekokkvoll/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Jannicke Johansen",
            //      "url": "https://oiw.no/speaker/jannicke-johansen",
            //      "@type": "EducationGroup",
            //      "@id": "sNzj4K0edIxTY7alYl8RbF",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "mesh-community-stage-factiverse",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Community",
            //      "Community Stage"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Book launch: Gamification in practise (and for the win!) | Mesh Community Stage Sending event 'Book launch: Gamification in practise (and for the win!) | Mesh Community Stage' to importer
            //{
            //  "name": "💪 Bruce Studios x Mesh: HIIT Event",
            //  "url": "https://oiw.no/event/bruce-studios-x-mesh",
            //  "startDate": "2024-09-26T05:30:00.000Z",
            //  "localStartDate": "2024-09-26T07:30",
            //  "endDate": "2024-09-26T06:30:00.000Z",
            //  "location": {
            //    "name": "Mesh Youngstorget, Atrium",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9139703,
            //      "longitude": 10.7439046
            //    },
            //    "@type": "Place",
            //    "@id": "417gYJuiDyI1J1z4fkq71e",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Bruce Studios",
            //      "url": "https://www.brucestudios.com",
            //      "@id": "n7pDqjz27FZZSwsInLmj5K",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/007e4eaa33d6fb7954b4300689af8ef79744ce11-400x400.png",
            //      "sameAs": [
            //        "https://www.brucestudios.com"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.brucestudios.com"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "bruce-studios-x-mesh",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Fitness \u0026 Wellbeing",
            //      "Sport"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] 💪 Bruce Studios x Mesh: HIIT Event Sending event '💪 Bruce Studios x Mesh: HIIT Event' to importer
            //{
            //  "name": "Switch Conference",
            //  "url": "https://oiw.no/event/switch-conference24",
            //  "startDate": "2024-09-25T06:30:00.000Z",
            //  "localStartDate": "2024-09-25T08:30",
            //  "endDate": "2024-09-25T10:00:00.000Z",
            //  "description": "https://www.linkedin.com/search/results/all/?fetchDeterministicClustersOnly\u003dtrue\u0026heroEntityKey\u003durn%3Ali%3Aorganization%3A92487525\u0026keywords\u003dswitch%20conference%20no\u0026origin\u003dRICH_QUERY_SUGGESTION\u0026position\u003d0\u0026searchId\u003da96dd6b1-c76a-4e0b-965e-2884efb3a180\u0026sid\u003dXSK\u0026spellCorrectionEnabled\u003dfalse",
            //  "location": {
            //    "name": "Schjødt advokat firma lokaler",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9114034,
            //      "longitude": 10.735134
            //    },
            //    "@type": "Place",
            //    "@id": "hQLKYFGzcQ7wYQCuKmUaIF",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Kjeller Innovasjon",
            //      "url": "https://www.kjellerinnovasjon.no/",
            //      "@id": "hQLKYFGzcQ7wYQCuKmVB6V",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/68722a5774480672c0109f849006cdded596fec2-405x124.png",
            //      "sameAs": [
            //        "https://www.kjellerinnovasjon.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.kjellerinnovasjon.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Kongsberg Innovasjon",
            //      "url": "https://kongsberginnovasjon.no/",
            //      "@id": "ia7ghcj6Cj8sG7Ap3ziErh",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/f31d3f9dd2978e2d95816589961db57038e8de87-390x129.png",
            //      "sameAs": [
            //        "https://kongsberginnovasjon.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://kongsberginnovasjon.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Smart Innovation Norway",
            //      "url": "https://smartinnovationnorway.com/no/",
            //      "@id": "8UA3n3hYR4VMQWTBzfvwgO",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/c15357fe2426857be614019be9d8e9faff5e9a2e-311x162.png",
            //      "sameAs": [
            //        "https://smartinnovationnorway.com/no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://smartinnovationnorway.com/no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Kenneth Bodahl",
            //      "url": "https://oiw.no/speaker/kenneth-bodahl",
            //      "description": "Kenneth Bodahl is the CEO of the technology company Pixii, which provides battery-based energy storage solutions to clients worldwide. The company has been recognized as Norway\u0027s fastest-growing technology company in Deloitte\u0027s Fast 50.\n\n\n\n\n\n\n\n\n\n\n\n\n",
            //      "@type": "EducationGroup",
            //      "@id": "ia7ghcj6Cj8sG7Ap3ziv2N",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/4df39a0b7023c0049aa6bc01b963e9b8d1c6ee93-200x200.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Peter Barrett",
            //      "url": "https://oiw.no/speaker/peter-barrett",
            //      "description": "Peter Barrett dropped out of Sydney uni to head to Silicon Valley in 1986. He gave Elon Musk his first job in the Valley, worked with Bill Gates, and started a venture fund, Playground Global, that has had 20% of its portfolio hit billion-dollar valuations. ",
            //      "@type": "EducationGroup",
            //      "@id": "uifTdwBj65JXb9pObqHkha",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/e019cae9faa04da34e778102c1f9a773b6be1a8b-263x191.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Torkel Engerness",
            //      "url": "https://oiw.no/speaker/torkel-engerness",
            //      "@type": "EducationGroup",
            //      "@id": "uifTdwBj65JXb9pObqlnY1",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/4fdcde236414553232c62b081c695752d306dd34-720x480.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Reetta Heiskanen",
            //      "url": "https://oiw.no/speaker/reetta-heiskanen",
            //      "@type": "EducationGroup",
            //      "@id": "L1QlNrpdCbsRFieBF9mMx8",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/cbd71574a1f2ce1c7affda22a5f844fcdbb6d44d-2560x1707.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Hilde Holdhus",
            //      "url": "https://oiw.no/speaker/hilde-holdhus",
            //      "@type": "EducationGroup",
            //      "@id": "8UA3n3hYR4VMQWTBziJgsQ",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/8da004d911782eb8a3e0e0bc949e38a8a5a7f74b-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Daniel Abicht",
            //      "url": "https://oiw.no/speaker/daniel-abicht",
            //      "@type": "EducationGroup",
            //      "@id": "L1QlNrpdCbsRFieBF9nuRc",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ceb65578f05c7a2fadb6606569321a65a9fc6ad6-1875x1546.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "switch-conference24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "DeepTech",
            //      "Investment",
            //      "Conference"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Switch Conference Sending event 'Switch Conference' to importer
            //{
            //  "name": "Climate Tech Investor Dinner",
            //  "url": "https://oiw.no/event/climate-tech-investor-dinner24",
            //  "startDate": "2024-09-23T17:30:00.000Z",
            //  "localStartDate": "2024-09-23T19:30",
            //  "endDate": "2024-09-23T19:30:00.000Z",
            //  "description": "The Climate Tech Investor Dinner is a collaboration between We Are Human and Oslo Business Region. Inspired by EntrepreneurShipOne, innovators, entrepreneurs, and investors convene to shape sustainable solutions. Hosted by Johan Brand, Sindre Østgaard, and Tonje Ørnholt.",
            //  "location": {
            //    "name": "The Conduit Oslo",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "@type": "Place",
            //    "@id": "d4b4b513-db4c-4f8a-a266-fa9d92dddf74",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Oslo Business Region",
            //      "url": "https://oslobusinessregion.no/",
            //      "@id": "1416bb50-5883-4cd9-9b01-cab5a693c76e",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/9baac37fcdffee8319eff9a617ef3253b59c0ff2-800x600.png",
            //      "sameAs": [
            //        "https://oslobusinessregion.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oslobusinessregion.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "We Are Human",
            //      "url": "https://www.wearehuman.cc/",
            //      "@id": "c120fe8f-2c7f-4b86-874a-c242122eb531",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/95a3c82644fb8e2e63ba8da0394bc844649beb5d-170x74.svg",
            //      "sameAs": [
            //        "https://www.wearehuman.cc/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.wearehuman.cc/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "climate-tech-investor-dinner24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "ClimateTech",
            //      "Investment",
            //      "Networking"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Climate Tech Investor Dinner Sending event 'Climate Tech Investor Dinner' to importer
            //{
            //  "name": "Building a winning Nordic ecosystem for impact startups",
            //  "url": "https://oiw.no/event/obr24",
            //  "startDate": "2024-09-25T07:00:00.000Z",
            //  "localStartDate": "2024-09-25T07:00:00.000Z",
            //  "endDate": "2024-09-25T08:00:00.000Z",
            //  "location": {
            //    "name": "T3, Tordenskiolds gate 3",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": " 0160 Oslo",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.913164,
            //      "longitude": 10.7336859
            //    },
            //    "@type": "Place",
            //    "@id": "998c9697-8d53-4c11-b713-3feded08613f",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Business Helsinki",
            //      "url": "https://www.helsinkipartners.com/",
            //      "@id": "bc68f3c1-51d5-4cd9-884f-b8c3f62cc48e",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/b64e272daa9dd7171fafa53dcdb97c6ac1c93b52-698x357.png",
            //      "sameAs": [
            //        "https://www.helsinkipartners.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.helsinkipartners.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Stockholm Business Region",
            //      "url": "https://www.stockholmbusinessregion.com/",
            //      "@id": "14f73c7e-a856-4095-a3d3-8d40018c6e9b",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/9960b8bd534c0f7735abb33a8a0a3cec22cb5b08-500x237.png",
            //      "sameAs": [
            //        "https://www.stockholmbusinessregion.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.stockholmbusinessregion.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Oslo Business Region",
            //      "url": "https://oslobusinessregion.no/",
            //      "@id": "1416bb50-5883-4cd9-9b01-cab5a693c76e",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/9baac37fcdffee8319eff9a617ef3253b59c0ff2-800x600.png",
            //      "sameAs": [
            //        "https://oslobusinessregion.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oslobusinessregion.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Sarita Runeberg",
            //      "url": "https://oiw.no/speaker/maria-runeberg",
            //      "description": "Sarita Runeberg\n is set to steer Maria 01 into a promising new era, building upon the company’s successful achievements in managing the Nordics’ leading startup campus and community. Runeberg’s previous roles at Smartly.io, the world’s largest SaaS digital advertising platforms, and Reaktor, a global technology company designing and building digital products and services, have been marked by an unwavering dedication to fostering innovation, driving growth and leading teams to success.",
            //      "@type": "EducationGroup",
            //      "@id": "a2345c86-13b6-4343-afea-153fd0b37a02",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/0c0bf30986f7f55e58884522a1fec160ea9e8899-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Anna Munthe-Kaas",
            //      "url": "https://oiw.no/speaker/anna-munthe-kaas",
            //      "description": "Antler is a global early-stage VC that backs founders from day zero to greatness by providing unique access to co-founder matching, business validation, and pre-seed capital. Besides being the earliest backer for startups, we are also a long-term capital partner that provides expansion support and scale-up funding to breakout companies from Series A onwards. Anna Munthe-Kaas is an associate partner with a background in start-ups and large tech environments.",
            //      "@type": "EducationGroup",
            //      "@id": "29f9233d-0a29-4595-86e2-6ecaf2b7fcdf",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ae9484fc8e933f541d5d504f55208fb686eb4d92-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Viktoria Hensler",
            //      "url": "https://oiw.no/speaker/viktoria-hensler",
            //      "description": "Viktoria is a Stockholm-based Berliner with five years of experience in the startup ecosystem, previously working in the tech and music industries.\nCurrently, she serves as the Nordic Ecosystem Lead at Norrsken, where she supports the Stockholm House community of impact founders by providing access to capital, networks, and knowledge. Her efforts aim to empower founders to potentially grow into the next generation of impact unicorns.\nIn her previous role, she was the NDRC Programme Director at Dogpatch Labs, where she managed all NDRC early-stage startup programs, including Pre-Accelerators, Founder Weekends, Office Hours, and Masterclasses for startup ecosystem builders.\nWith a B.A. in Media and Communication Studies and an M.Sc. in Management, she is passionate about communication strategy, innovation, and technology.",
            //      "@type": "EducationGroup",
            //      "@id": "fd176e15-5928-48f9-ada0-47a4b607a7d0",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/4f448a8fda559974834b076417f94aa00a5b3e6d-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Lars Johan Bjørkevoll",
            //      "url": "https://oiw.no/speaker/lars-johan-bjørkevoll",
            //      "@type": "EducationGroup",
            //      "@id": "NK00FF53hrTwhTsNxPYvQP",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/cf0b26227308c778e501996908b278b9ac3b4a8d-2246x1891.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "obr24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Scaling",
            //      "Community",
            //      "Investment",
            //      "Panel"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Building a winning Nordic ecosystem for impact startups Sending event 'Building a winning Nordic ecosystem for impact startups' to importer
            //{
            //  "name": "Who\u0027s (whose) right ? Protecting creations in the age of AI",
            //  "url": "https://oiw.no/event/patentstyret24",
            //  "startDate": "2024-09-24T06:00:00.000Z",
            //  "localStartDate": "2024-09-24T08:00",
            //  "endDate": "2024-09-24T08:00:00.000Z",
            //  "description": "",
            //  "location": {
            //    "name": "Forskningsparken",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9421409,
            //      "longitude": 10.7139813
            //    },
            //    "@type": "Place",
            //    "@id": "hQLKYFGzcQ7wYQCuKrWPLR",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Forskningsparken",
            //      "url": "https://www.forskningsparken.no/en",
            //      "@id": "PygGVB9TYoPQnjIowF1WeS",
            //      "sameAs": [
            //        "https://www.forskningsparken.no/en"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.forskningsparken.no/en"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Patentstyret",
            //      "url": "https://www.patentstyret.no/",
            //      "@id": "ia7ghcj6Cj8sG7Ap3yC9Ka",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/5e4af262a2906b6630de5c24273ee4b01ba0aae7-2142x1284.png",
            //      "sameAs": [
            //        "https://www.patentstyret.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.patentstyret.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Benedikte Wallace",
            //      "url": "https://oiw.no/speaker/benedikte-wallace",
            //      "description": "Benedikte Wallace is a postdoctoral researcher at the RITMO Centre for Interdisciplinary Studies of Rhythm, Time, and Motion at the University of Oslo. Her work explores human-AI interaction through generative machine learning and the use of AI as a tool for pursuing and understanding creativity.",
            //      "@type": "EducationGroup",
            //      "@id": "jjBq4X6gdc6IuElj7mvXqj",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/fc549b51fb166ee444d92b9c201bde7e57e911c2-1258x1475.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Benita McKay",
            //      "url": "https://oiw.no/speaker/benita-mckay",
            //      "description": "Benita is both a part qualified European and UK patent attorney, specialising in protecting AI and software innovations. She has a PhD in applying AI to physics and bioengineering, and trained in one of Europe\u0027s top intellectual property firms before joining Bryn Aarflot\u0027s award winning patent team. ",
            //      "@type": "EducationGroup",
            //      "@id": "PD0p2pDN3sZp4xmEwkBoQs",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/a6aa8330842b1120eb55f1554a03a388b7f3982e-2560x2560.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Cullen Miller",
            //      "url": "https://oiw.no/speaker/cullen-miller1",
            //      "description": "Cullen is an engineer, spatial media designer, and musician based in Berlin (\u0026 San Francisco, sometimes).\nHe is currently the VP of Policy at Spawning, an organization building data governance solutions for AI training datasets. Previously he helped design and engineer the technical systems for large-scale immersive media-based architecture project.",
            //      "@type": "EducationGroup",
            //      "@id": "DFEKOVjeVqrEbThYrixAKY",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/c64705a100ca3ace8238a7670f08760253531c09-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Silvija Seres",
            //      "url": "https://oiw.no/speaker/silvija-seres",
            //      "description": "Silvija is a mathematician and a technology investor. She has worked on algorithm research in Oxford, development for the search engine Alta Vista in Palo Alto, strategic leadership in Fast Search and Transfer in Oslo and Boston, and later in Microsoft. She now works as a board member in several major companies such as DNV and Ruter, and as an activist.",
            //      "@type": "EducationGroup",
            //      "@id": "SDLUuTWhTFC5UwnLL5B4ms",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/99d124c01186ba5963bb837a1e3e703b7543a9c4-700x466.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "patentstyret24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Creative Tech",
            //      "Networking",
            //      "Seminar"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Who's (whose) right ? Protecting creations in the age of AI Sending event 'Who's (whose) right ? Protecting creations in the age of AI' to importer
            //{
            //  "name": "(SOLD OUT) Startuplab Climate Tech Summit ",
            //  "url": "https://oiw.no/event/climatetech-summit24",
            //  "startDate": "2024-09-24T06:30:00.000Z",
            //  "localStartDate": "2024-09-24T08:30",
            //  "endDate": "2024-09-24T13:00:00.000Z",
            //  "description": "Norway\u0027s largest climate tech startup stage",
            //  "location": {
            //    "name": "Startuplab",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9419998,
            //      "longitude": 10.7143606
            //    },
            //    "@type": "Place",
            //    "@id": "hQLKYFGzcQ7wYQCuKsWroB",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Startuplab",
            //      "url": "https://www.startuplab.com/",
            //      "@id": "clo3OfcAIt4G4RsnL8DEcq",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/2ddebbf91994d8f6f28b377360d1ea8ceea26a9e-4096x2152.png",
            //      "sameAs": [
            //        "https://www.startuplab.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.startuplab.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Danijel Višević",
            //      "url": "https://oiw.no/speaker/danijel-višević",
            //      "description": "Danijel Višević is a General Partner at World Fund. He is a recognised climate tech thought leader, policy professional and journalist by vocation.\n\nBefore co-founding World Fund, he was Director of Communications at Project A Ventures, co-founder of Zetra Project and Krautreporter.",
            //      "@type": "EducationGroup",
            //      "@id": "YCgKmZHkZ9BdJmd5Z2On1r",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/75162003d9d9767c581315eec96697f2b7f53dcd-839x1024.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Madelene Larsson",
            //      "url": "https://oiw.no/speaker/madelene-larsson",
            //      "description": "Madelene Larsson is a Principal at Giant and Head of the Climate and Deeptech practice. Previously, Madelene was a product owner at Revolut.\n\nBefore Revolut, Madelene spent six years at J.P. Morgan, where she specialised in assisting technology companies in their journey to go public. ",
            //      "@type": "EducationGroup",
            //      "@id": "Pco71BhnOp9iD3usLyD9nz",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/5a68e292f62456bbdb94c21a33c78f196a28961e-3500x3500.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Karl Liapunov",
            //      "url": "https://oiw.no/speaker/karl-liapunov",
            //      "description": "Karl is the Head of Energy and Climate at Startuplab and leads the Climate Tech Summit. \n\nPrior to joining Startuplab in 2021, he spent several years as an energy tech investment banker in Houston, Texas. ",
            //      "@type": "EducationGroup",
            //      "@id": "ZARUrvtActr8bNkcuLrByD",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/b305522d7819edf88d1c7c0e4ec41e817aad0044-506x506.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Jonas Helmikstøl",
            //      "url": "https://oiw.no/speaker/jonas-helmikstøl",
            //      "description": "Jonas has 15 years of experience in tech and management, and helped build both Zaptec and Easee. \n\nJonas built Easee from 0 to over 500 employees and led the company through the crisis of 2023. He has now started the startup builder Fyrstikk, which aims to create companies that will deliver sustainable and scalable concepts within the energy sector",
            //      "@type": "EducationGroup",
            //      "@id": "1O6UfVeEhrCXNCgCQe55FG",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/c6adf230714767694774e568abe747ed004af5d4-1591x2250.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Maynard Holt",
            //      "url": "https://oiw.no/speaker/maynard-holt",
            //      "description": "Maynard Holt is the Founder \u0026 CEO of Veriten - an energy-focused knowledge platform that brings diverse perspectives to the energy transition discussion. \n\nMaynard previously served as CEO of Tudor, Pickering, Holt, and before that was an MD at Goldman Sachs, resulting in +27 years of experience in energy investment banking and strategic advice. ",
            //      "@type": "EducationGroup",
            //      "@id": "TzD7qyCW5nTWLW6ytB4iS9",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/3035c425f59802e57b16ac0137c6398a299f54a8-856x1000.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Fredrik Cassel",
            //      "url": "https://oiw.no/speaker/fredrik-cassel",
            //      "description": "Fredrik Cassel is a General Partner at Creandum, one of Europe’s most experienced investors. Fredrik was an early backer of several companies that have grown to dominate their respective markets, such as Spotify, Depop, Kahoot!, KRY / LIVI, and Virta Health. Fredrik has made regular appearances on the European and global MIDAS List.",
            //      "@type": "EducationGroup",
            //      "@id": "29cfuvScEVHxUPeK7i4ucG",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/cb1954ec104d7d21c7d1798fb5df1df7116d2c08-2465x3150.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Sigrun Gjerløw Aasland",
            //      "url": "https://oiw.no/speaker/sigrun-gjerløw-aasland",
            //      "description": "Sigrun Gjerløw Aasland is the State Secretary in the Ministry of Climate and Environment. \n\nShe has previously served as the CEO of ZERO, the Deputy Director of the think tank Agenda, and has a background from Econ, Norfund, as Managing Director of the analytics company Damvad, and from the World Bank.\n\n\n\n\n",
            //      "@type": "EducationGroup",
            //      "@id": "JxpKUxUWSDPhcfPlpejtIV",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/8af44c2597ec41f8b897567122330e3b757a09a2-1024x1024.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Harrshinny Vallipuram",
            //      "url": "https://oiw.no/speaker/harrshinny-vallipuram-",
            //      "description": "Harrshinny joined Startuplab team in June 2023 as Incubator Coordinator - bringing an expertise in entrepreneurship and a strong commitment to supporting female entrepreneurs. She completed her masters at the Norwegian School of Entrepreneurship in 2022 while working at Boost Henne NTNU.",
            //      "@type": "EducationGroup",
            //      "@id": "vd41e4YzH4Vh3Umcvwnghe",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/33f0369fd528639fc058196de9728ff55b95fc21-200x200.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "climatetech-summit24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "ClimateTech",
            //      "Investment",
            //      "Summit"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] (SOLD OUT) Startuplab Climate Tech Summit  Sending event '(SOLD OUT) Startuplab Climate Tech Summit ' to importer
            //{
            //  "name": "DNB NXT - Where ideas and capital meet",
            //  "url": "https://oiw.no/event/dnb-nxt24",
            //  "startDate": "2024-09-26T08:00:00.000Z",
            //  "localStartDate": "2024-09-26T10:00",
            //  "endDate": "2024-09-26T16:30:00.000Z",
            //  "description": "facebook: DNB\nLinkedIn: DNB\nInstagram: @dnb_Bank\n#DNB NXT",
            //  "location": {
            //    "name": "DNB Bjørvika",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "@type": "Place",
            //    "@id": "74arqP7dLrtNNRxiirEyYY",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "DNB BANK ASA",
            //      "url": "https://www.dnb.no/bedrift/arrangementer/dnb-nxt",
            //      "@id": "FoG5xwe2L9CgE3lTi0ozOI",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/3281dcef0cb31635be63e11f3475e69c2fdc2aa8-800x800.png",
            //      "sameAs": [
            //        "https://www.dnb.no/bedrift/arrangementer/dnb-nxt"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.dnb.no/bedrift/arrangementer/dnb-nxt"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Jørn Lyseggen",
            //      "url": "https://oiw.no/speaker/jørn-lyseggen",
            //      "description": "Jørn Lyseggen is a Norwegian serial entrepreneur, patent inventor, and the founder and Executive Chairman of Meltwater and the Meltwater Entrepreneurial School of Technology (MEST). As of 2023, Meltwater employed over 2300 staff in over 50 cities and 25 countries while generating revenue of over $430 million.",
            //      "@type": "EducationGroup",
            //      "@id": "1O6UfVeEhrCXNCgCQr4taC",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/2365ce37fd67706376bcd409329c1d37c4988a90-544x698.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Deqa Abukar",
            //      "url": "https://oiw.no/speaker/deqa-abukar",
            //      "description": "Deqa Abukar, an entrepreneur recognized on the Forbes 30 under 30 list, is the founder of Bling Startup and BLING Capital. She’s dedicated to empowering underrepresented entrepreneurs and advancing women in business. Her significant contributions have earned her prestigious awards. ",
            //      "@type": "EducationGroup",
            //      "@id": "1O6UfVeEhrCXNCgCQrCgnS",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ff78736a66f477c4c4f5770a9ef1af714fe02675-544x698.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Magnus Grimeland",
            //      "url": "https://oiw.no/speaker/magnus-grimeland",
            //      "description": "Magnus, CEO of Antler, is dedicated to nurturing world-changing companies. He co-founded Zalora and served as COO at GFG. With a background from Harvard and McKinsey, his experience spans telecom, media, and tech industries.",
            //      "@type": "EducationGroup",
            //      "@id": "u2C9UrQhuoL98Wo7be160G",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/96227da056bbe534806e78f8aab10d2b381f752d-544x698.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Anders Kvåle",
            //      "url": "https://oiw.no/speaker/anders-kvåle",
            //      "description": "Anders is the founder of Spacemaker, a company that helps architects and developers maximize the value of land plots. Today, the company has a total of 115 employees and offices in 8 countries.",
            //      "@type": "EducationGroup",
            //      "@id": "VBlNaGbxeXMiFaeMV5pQ5i",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/3d54f244ce69141d1579606b2e8c401404d7fb0f-272x349.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Kristin Aamodt",
            //      "url": "https://oiw.no/speaker/kristin-aamodt",
            //      "description": "Kristin is a trained engineer with extensive experience as an investor in European markets.",
            //      "@type": "EducationGroup",
            //      "@id": "VJwDF6OhsypgL07DCS7Ufg",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/91fa9aed653773280b129a84a6848afc5f00945f-272x349.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Marte Sootholtet",
            //      "url": "https://oiw.no/speaker/marte-sootholtet1",
            //      "description": "Marte is the CEO and the architect behind Impact StartUp Norway, which helps companies succeed in creating a more sustainable business environment.",
            //      "@type": "EducationGroup",
            //      "@id": "29cfuvScEVHxUPeK7hlWtM",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/5df3478030f77f8530b78eeeccbb614a0a43e3c4-272x349.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Aina Lemoen Lunde",
            //      "url": "https://oiw.no/speaker/aina-lemoen-lunde",
            //      "description": "Aina has over 20 years of experience in leadership, sales, and marketing across various industries and sectors. She has published several award-nominated professional books and is the founder of #Huninvesterer from DNB.",
            //      "@type": "EducationGroup",
            //      "@id": "VBlNaGbxeXMiFaeMV5roGI",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/62bf2cc84f88061b82c3174c68642379c2d9373f-272x349.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Anne Dingstad",
            //      "url": "https://oiw.no/speaker/anne-dingstad",
            //      "description": "Anne is the CEO of Saga Robotics, which operates sustainable precision agriculture with the robot Thorvald in the UK/US. Anne has extensive experience in building and scaling technology companies internationally",
            //      "@type": "EducationGroup",
            //      "@id": "i25yJA0neSgT1BpxaIABS1",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/b73669defcbbc7aeee4f412e975442be6caa95ea-544x698.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Bjørn Arve Ofstad",
            //      "url": "https://oiw.no/speaker/bjørn-arve-ofstad",
            //      "description": "Bjørn Arve leads NG Group and has over 20 years of leadership experience in publicly listed and private equity-owned companies at both operational and strategic levels.",
            //      "@type": "EducationGroup",
            //      "@id": "i25yJA0neSgT1BpxaIAK2o",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/c42677bc29caa87931347667d9aac9b3c8d3d1cb-544x698.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Håkon Haugli",
            //      "url": "https://oiw.no/speaker/håkon-haugli1",
            //      "description": "Håkon Haugli, CEO of Innovation Norway since 2019, is an experienced business leader and former politician.",
            //      "@type": "EducationGroup",
            //      "@id": "i25yJA0neSgT1BpxaIAsre",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/7022a1f6af86805a964b022042ca53d3a05e666e-544x698.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "dnb-nxt24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Investment",
            //      "Networking",
            //      "Business Conference"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] DNB NXT - Where ideas and capital meet Sending event 'DNB NXT - Where ideas and capital meet' to importer
            //{
            //  "name": "We need to talk about money",
            //  "url": "https://oiw.no/event/we-need-to-talk-about-money24",
            //  "startDate": "2024-09-24T07:00:00.000Z",
            //  "localStartDate": "2024-09-24T09:00",
            //  "endDate": "2024-09-24T08:30:00.000Z",
            //  "description": "Are you a business leader or owner who wants to unlock the full potential of your company\u0027s financial data? Then you can\u0027t afford to miss this exclusive breakfast event during Oslo Innovation Week!\n\n🌟 \"We Need to Talk About Money\" 🌟\n\nIn today’s fast-paced business landscape, understanding and leveraging your company\u0027s financial numbers is more crucial than ever. This event brings together top minds in finance and investment to discuss how companies can harness the power of financial insights to fuel growth, innovation, and success. 🚀",
            //  "location": {
            //    "name": "KPMG - Sørkedalsveien 6 in Oslo",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9301167,
            //      "longitude": 10.7095542
            //    },
            //    "@type": "Place",
            //    "@id": "EID7htTIDezvC7ki0Xg4UE",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "KPMG  ",
            //      "url": "https://kpmg.com/no/nb/home.html",
            //      "@id": "Idkx92uKNUX2SImbfGhm5o",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/35aae8009f1410a90b1b9566f567778f2ba4073e-300x150.png",
            //      "sameAs": [
            //        "https://kpmg.com/no/nb/home.html"
            //      ],
            //      "gogo": {
            //        "webpage": "https://kpmg.com/no/nb/home.html"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Capassa",
            //      "url": "https://www.capassa.com/",
            //      "@id": "S8WVnhHTDNMkkNUC8ZqbBz",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/16fbf1cb32717db116ac3ba815181546bc02d2ef-230x50.png",
            //      "sameAs": [
            //        "https://www.capassa.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.capassa.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Skyfall Ventures",
            //      "url": "https://www.skyfall.vc/",
            //      "@id": "dP6x217tf7raGk5klhd2qS",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/b12862a073b4f17c281d722570fa24076d4a3255-3000x738.png",
            //      "sameAs": [
            //        "https://www.skyfall.vc/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.skyfall.vc/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Investinor",
            //      "url": "https://investinor.no/",
            //      "@id": "NO7Mjjgfso2MMqbm3iXfCB",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/1deb630d4585c7ada3a18402c9ee1f6d6f6c5d32-3x1.svg",
            //      "sameAs": [
            //        "https://investinor.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://investinor.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Karianne Vintervoll",
            //      "url": "https://oiw.no/speaker/karianne-vintervoll",
            //      "@type": "EducationGroup",
            //      "@id": "EID7htTIDezvC7ki0XgjVS",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/9a4296bc5b80dbbe3e67c2628c98df2911df7e87-196x183.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Marit Wetterhus",
            //      "url": "https://oiw.no/speaker/marit-wetterhus",
            //      "description": "Marit, CEO of Capassa, pioneers SME finance with a Digital CFO. With 25+ years as CEO, Board Member and investor, she drives innovation. Marit is also a Tech Nordic Advocates Global Board Member, an Advisor to the OECD and a co-founder of Women in Tech initiative. She has an MBA from NHH and a Bachelor\u0027s from Carleton University in Canada.",
            //      "@type": "EducationGroup",
            //      "@id": "gtrSyynP7P1x0DLKmoDmu0",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/90eec473c654148c270e87c0a1e6514fc8b0c407-567x709.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Cecilie Skjong",
            //      "url": "https://oiw.no/speaker/cecilie-skjong",
            //      "@type": "EducationGroup",
            //      "@id": "1O6UfVeEhrCXNCgCQfteDA",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/c6e8bd72dd319b8ad11e2b6a2f3a26561fdf148d-6016x4016.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Patrick Sandahl",
            //      "url": "https://oiw.no/speaker/patrick-sandahl",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "Q01A6nB0EzzzgHsi77oECI",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/158d143d0fcc92db72ac50728d4aa0b178a78b20-469x532.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "we-need-to-talk-about-money24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "FinTech",
            //      "Investment",
            //      "Business Conference"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] We need to talk about money Sending event 'We need to talk about money' to importer
            //{
            //  "name": "OIW Official Afterparty at Strøget",
            //  "url": "https://oiw.no/event/reodor-afterparty24",
            //  "startDate": "2024-09-26T15:00:00.000Z",
            //  "localStartDate": "2024-09-26T17:00",
            //  "endDate": "2024-09-26T23:00:00.000Z",
            //  "location": {
            //    "name": "Reodor Studios",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.914149,
            //      "longitude": 10.7458565
            //    },
            //    "@type": "Place",
            //    "@id": "hQLKYFGzcQ7wYQCuKmpusF",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Reodor Studios",
            //      "url": "https://www.reodorstudios.com/",
            //      "@id": "clo3OfcAIt4G4RsnL3pCUZ",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/d3585c1e97ccc68cc36932f6af9df4a3eefcae82-999x117.png",
            //      "sameAs": [
            //        "https://www.reodorstudios.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.reodorstudios.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Nora Collective",
            //      "url": "https://www.noracollective.no/",
            //      "@id": "VBlNaGbxeXMiFaeMV6B0vy",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/d39a39b1a66a1b5fb94e798748c8f700548114a2-2213x1213.png",
            //      "sameAs": [
            //        "https://www.noracollective.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.noracollective.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Oslo Innovation Week",
            //      "url": "https://oiw.no/",
            //      "@id": "toREhxqQ42TEspzFxelzuW",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/92eee2b3af2cf96baf3b994ca8c0214b70b0a6aa-3167x2001.png",
            //      "sameAs": [
            //        "https://oiw.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "reodor-afterparty24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Afterparty",
            //      "Networking"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] OIW Official Afterparty at Strøget Sending event 'OIW Official Afterparty at Strøget' to importer
            //{
            //  "name": "Brand strategy and execution: Psychology driving sales",
            //  "url": "https://oiw.no/event/motivation-branding24",
            //  "startDate": "2024-09-25T13:00:00.000Z",
            //  "localStartDate": "2024-09-25T15:00",
            //  "endDate": "2024-09-25T14:30:00.000Z",
            //  "description": "Behavior is irrelevant. Motivation is everything. Yes, they buy your product. But why do they do it? The answer is in understanding human psychology.\n\nLearn from the best on branding strategy, digital execution and get practical examples. In this event you will learn from people who have worked both on big global brands and small startups.",
            //  "location": {
            //    "name": "Business Village",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9105443,
            //      "longitude": 10.727497
            //    },
            //    "@type": "Place",
            //    "@id": "HwoXp8ilMKl1Jylzx26LEd",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Motivation Branding",
            //      "url": "https://motivationbranding.com/",
            //      "@id": "SCllQO5XG2qM2tRBVOSODP",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/85269fca1024c4b0fc315e01e0ec97a67738094c-1749x645.png",
            //      "sameAs": [
            //        "https://motivationbranding.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://motivationbranding.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Fullstakk",
            //      "url": "https://www.fullstakk.no/",
            //      "@id": "4V54Rf0BwbIyLMde5XhuNi",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/02a0054ebe28347b3d70e72598070f59b83d819b-1916x720.png",
            //      "sameAs": [
            //        "https://www.fullstakk.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.fullstakk.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Aker Brygge Business Village",
            //      "@id": "z3L3xjQSUzI9kbu7ZPRA6q",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/787e73547952482238a6a1ceaf41e51a466b068c-1080x1065.png",
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Kristian W Majer",
            //      "url": "https://oiw.no/speaker/kristian-w-majer",
            //      "description": "Kristian W. Majer is CEO of Motivation Branding and has worked globally on brands like IKEA, Coca-Cola, reMarkable, Autostore, Xplora, VG and many more. He also works as a visiting professor and serves as an Executive in residence at StartupLab.",
            //      "@type": "EducationGroup",
            //      "@id": "4V54Rf0BwbIyLMde5XhSYi",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/85ab89f9989a66586d2f48d3a7f4bf1988167cdc-240x240.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Fridtjof Hødnebø",
            //      "url": "https://oiw.no/speaker/fridtjof-hødnebø-",
            //      "description": "Fridtjof Hødnebø is CEO of Fullstakk,  a growth marketing agency, and has worked with a broad range of startups and scaleups, like Nettbil, Fleks and Hjemmelegene. Fridtjof is ex-Google, and also a visiting lecturer in marketing at Toulouse School of Management.",
            //      "@type": "EducationGroup",
            //      "@id": "SCllQO5XG2qM2tRBVORfGJ",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/7831a94797f03f9df625bd6e93386e76577ab34e-2030x2791.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Tonje Hallén Askim",
            //      "url": "https://oiw.no/speaker/tonje-hallén-askim",
            //      "description": "Tonje Hallén Askim is Growth Manager in Fullstakk, a growth marketing agency. She is an experienced strategic advisor, with expertise in content and social media from among others Storyhouse Egmont, Redink/HyperRedink and most recently as CEO of Elg. Tonje was involved in the launch of Telia Startup, and has worked with a variety of brands from COO",
            //      "@type": "EducationGroup",
            //      "@id": "sDETOVCKiPX6t4Op2Qwkeu",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ec6930f94914891690d825793828dfe09c63765a-2665x3665.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Julie Holst Berntsen",
            //      "url": "https://oiw.no/speaker/julie-holst-berntsen",
            //      "description": "Julie Holst Berntsen is Partner and Client Director in Motivation Branding and has worked with brands like Hermes, Levi’s, Swatch Group, Louis Vuitton, Gant, Procter and Gamble, Sony Music and many more. She is also a board member at the Norwegian Marketing Association and certified catalyst in Executive Growth Alliance.  ",
            //      "@type": "EducationGroup",
            //      "@id": "ZbkPLmbDQwTINNFTHA3p1p",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/dcee010626911662682e3119f17b9a457a6dfb63-1887x1965.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "motivation-branding24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Scaling",
            //      "Networking",
            //      "Talk"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Brand strategy and execution: Psychology driving sales Sending event 'Brand strategy and execution: Psychology driving sales' to importer
            //{
            //  "name": "Future-Ready B2B Sales: Unlock Your Growth Potential",
            //  "url": "https://oiw.no/event/scaleupxq24",
            //  "startDate": "2024-09-25T08:30:00.000Z",
            //  "localStartDate": "2024-09-25T10:30",
            //  "endDate": "2024-09-25T10:30:00.000Z",
            //  "description": "",
            //  "location": {
            //    "name": "T3 - Tordenskiolds gate 3",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.913164,
            //      "longitude": 10.7362608
            //    },
            //    "@type": "Place",
            //    "@id": "xBB7iBjw6W7EaSR6v9Bph2",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "ScaleupXQ",
            //      "url": "https://www.scaleupxq.com/",
            //      "@id": "29cfuvScEVHxUPeK7hLBNI",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/5bbe2a60ed68d8e3416ca072149cebf4971cb9c6-3753x800.png",
            //      "sameAs": [
            //        "https://www.scaleupxq.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.scaleupxq.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Chew on This",
            //      "url": "https://www.chewonthis.world/",
            //      "@id": "VBlNaGbxeXMiFaeMV6XUKg",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/1ca4ccd38329ab23cc03641a3d89315f3884e6ac-3930x1829.png",
            //      "sameAs": [
            //        "https://www.chewonthis.world/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.chewonthis.world/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Lars Johan Bjørkevoll",
            //      "url": "https://oiw.no/speaker/lars-johan-bjørkevoll",
            //      "@type": "EducationGroup",
            //      "@id": "NK00FF53hrTwhTsNxPYvQP",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/cf0b26227308c778e501996908b278b9ac3b4a8d-2246x1891.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Marcus Ravik",
            //      "url": "https://oiw.no/speaker/marcus-ravik",
            //      "@type": "EducationGroup",
            //      "@id": "29cfuvScEVHxUPeK7hsxl6",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/9ad2b5662130d3f2e1f26b651fa71975d7685459-715x849.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Camilla Kildal",
            //      "url": "https://oiw.no/speaker/camilla-kildal",
            //      "@type": "EducationGroup",
            //      "@id": "NO7Mjjgfso2MMqbm3iIJO9",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/40aa76bbd8f2d14676ff5188592758dc494533f2-2000x3000.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "scaleupxq24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Scaling",
            //      "Networking",
            //      "Workshop"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Future-Ready B2B Sales: Unlock Your Growth Potential Sending event 'Future-Ready B2B Sales: Unlock Your Growth Potential' to importer
            //{
            //  "name": "Sportstech Demo Day \u0026 Trends @TheFactory Accelerator",
            //  "url": "https://oiw.no/event/the-factory24",
            //  "startDate": "2024-09-25T10:00:00.000Z",
            //  "localStartDate": "2024-09-25T12:00",
            //  "endDate": "2024-09-25T12:00:00.000Z",
            //  "description": "Norway is leading the way in many sports and we see the emergence of many new sportstech entrepreneurs with innovative solutions for amateur and professional sports, athletes and clubs, as well as startups triggering activity and health.",
            //  "location": {
            //    "name": "TheFactory Accelerator \u0026 VC",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "@type": "Place",
            //    "@id": "DFEKOVjeVqrEbThYriviWA",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "TheFactory Accelerator \u0026 VC",
            //      "url": "https://www.thefactory.no/",
            //      "@id": "SDLUuTWhTFC5UwnLL54S9K",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/3c27e627f9750257f93241f6b02e90397571bc7b-1332x1334.png",
            //      "sameAs": [
            //        "https://www.thefactory.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.thefactory.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Jarle Aambø",
            //      "url": "https://oiw.no/speaker/jarle-aambø-aambø",
            //      "description": "Formerly Aambø held the positions as a National Team Coach and the Sports Director of the National Alpine Team for eight years, receiving 23 Olympic and World Champion medals - up from 0.\n\nDuring recent years, he has developed the Norwegian High Performance Cluste and he currently CEO of Igloo Innovation Sports Industry Cluster. ",
            //      "@type": "EducationGroup",
            //      "@id": "VdKfDJLLsUwX1ZJhWucqBY",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ecf8433e92450b04ede53a225d9fbc89a30a6229-1600x1067.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Cecilie Skjong",
            //      "url": "https://oiw.no/speaker/cecilie-skjong2",
            //      "@type": "EducationGroup",
            //      "@id": "VdKfDJLLsUwX1ZJhWucy4g",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/aa841e2f10acab9cfcca2ca4ee4df9dfd609c216-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Hans Christian Bjørne",
            //      "url": "https://oiw.no/speaker/hans-christian-bjørne",
            //      "description": "With more than 20 years of experience in the startup industry and as the co-founder of TheFactory Accelerator \u0026 VC, Hans Christian is now leading up the new Sportstech Academy program, helping Norwegian sportstechs on their scaling journeys. ",
            //      "@type": "EducationGroup",
            //      "@id": "VdKfDJLLsUwX1ZJhWue2vu",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/110f573907060c52de81219d16afff69f0dab587-545x545.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Andreas Olsen",
            //      "url": "https://oiw.no/speaker/andreas-olsen",
            //      "description": "Andreas Olsen was recently hired as the new CEO of the Norwegian scaleup Be Your Best, a VR-based precision and cognitive coach used and praised by more than 4.000 football players around the world. Andreas, has experience from Schibsted and several startups including Unacast and has also been an active investor in startups at Newmark capital.  ",
            //      "@type": "EducationGroup",
            //      "@id": "LMDtzdFP0V5rPqBZv0GAZP",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/d20bb4fe92ffcf42c6092c386decaf593e40089b-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Pippa Boothman",
            //      "url": "https://oiw.no/speaker/pippa-boothman",
            //      "description": "Pippa Boothman is the CEO of Playfinity, a Norwegian Active Gaming company on a mission to keep kids active by combining physical sports with digital gaming. She believes in the power of play to promote physical and mental health and has a passion for scaling products with a purpose. Pippa sits on the boards of various Norwegian companies.",
            //      "@type": "EducationGroup",
            //      "@id": "Q38qk3JW92Upy4cwM8KBJA",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/13ae4ea93a64e9b132fb8f2923239eb40423548c-1667x2500.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Line Flem Høst",
            //      "url": "https://oiw.no/speaker/line-flem-høst",
            //      "description": "Line Flem Høst is an Olympic medalist in sailing. She was born on 10 November 1995 in Oslo. She won a bronze medal at the 2020 Women\u0027s Laser Radial World Championship in Melbourne. She was selected to represent Norway at the 2024 Summer Olympics, where she won the bronze medal.",
            //      "@type": "EducationGroup",
            //      "@id": "JxpKUxUWSDPhcfPlpgYRPd",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/7453e2bf25288daf3c456f8ab62268b1dd3199e8-1224x746.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "the-factory24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Community",
            //      "Investment",
            //      "Pitch"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Sportstech Demo Day & Trends @TheFactory Accelerator Sending event 'Sportstech Demo Day & Trends @TheFactory Accelerator' to importer
            //{
            //  "name": "The Power of AI: Tools for Defending Human Rights ",
            //  "url": "https://oiw.no/event/oslo-freedom-forum24",
            //  "startDate": "2024-09-25T14:30:00.000Z",
            //  "localStartDate": "2024-09-25T16:30",
            //  "endDate": "2024-09-25T16:30:00.000Z",
            //  "location": {
            //    "name": "The Conduit",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9171611,
            //      "longitude": 10.7339606
            //    },
            //    "@type": "Place",
            //    "@id": "GBgGvFlp21zYJWMNefLJly",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Oslo Freedom Forum",
            //      "url": "https://oslofreedomforum.com/",
            //      "@id": "5SRznsJQzUNAXpNalZUTWQ",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/708b77f0a167131e798469492f4e51441d014f38-500x500.png",
            //      "sameAs": [
            //        "https://oslofreedomforum.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oslofreedomforum.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Digital Norway",
            //      "url": "https://digitalnorway.com",
            //      "@id": "GBgGvFlp21zYJWMNefLOS8",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/aac9322a98fe56842170d6a91bb66a8af40a18dd-2794x681.png",
            //      "sameAs": [
            //        "https://digitalnorway.com"
            //      ],
            //      "gogo": {
            //        "webpage": "https://digitalnorway.com"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Katapult",
            //      "url": "https://katapult.vc",
            //      "@id": "sNzj4K0edIxTY7alYl5wzz",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/c3b64bbe54eb29574de93454372cef3b89b98374-1536x678.png",
            //      "sameAs": [
            //        "https://katapult.vc"
            //      ],
            //      "gogo": {
            //        "webpage": "https://katapult.vc"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Jørn Haanæs",
            //      "url": "https://oiw.no/speaker/jørn-haanæs",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "sNzj4K0edIxTY7alYl6FBf",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/dc0339a23fce04014e685254b5606aabdba8518e-1536x1024.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Klement Ruey-sheng Gu",
            //      "url": "https://oiw.no/speaker/klement-ruey-sheng-gu-",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "sNzj4K0edIxTY7alYl70qK",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/2f255b14330543288fcdb536bac0c6f165c5348e-370x500.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Ilaria Carrozza",
            //      "url": "https://oiw.no/speaker/ilaria-carrozza",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "GBgGvFlp21zYJWMNefNLbA",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/d537622e9a61ea5265034a5dd81c9f20047c78ef-2223x3334.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Silvija Seres",
            //      "url": "https://oiw.no/speaker/silvija-seres1",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "sNzj4K0edIxTY7alYl79sV",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ffd136cd7a018dce0f89f1d6778fa432b39f4772-688x761.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "oslo-freedom-forum24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Networking",
            //      "Impact",
            //      "Panel"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] The Power of AI: Tools for Defending Human Rights  Sending event 'The Power of AI: Tools for Defending Human Rights ' to importer
            //{
            //  "name": "Investinor Investors Reception - by invitation only",
            //  "url": "https://oiw.no/event/investinor24",
            //  "startDate": "2024-09-26T14:00:00.000Z",
            //  "localStartDate": "2024-09-26T16:00",
            //  "endDate": "2024-09-26T16:00:00.000Z",
            //  "location": {
            //    "name": "I baren",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "@type": "Place",
            //    "@id": "3bSeV1kXTHmPOpL580Qbnq",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Investinor",
            //      "url": "https://investinor.no/",
            //      "@id": "nNlMDwdaFpeRBQSKRW0Hqu",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/0e7d44509d4cfd37844e5a44350ec4b48d4b61b8-950x707.jpg",
            //      "sameAs": [
            //        "https://investinor.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://investinor.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Atomico",
            //      "url": "https://atomico.com/",
            //      "@id": "aZRErKnzhCc3ad7aSGolAV",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/5f5f7d6dc575bc545a3c72792867e93888b72c54-600x120.png",
            //      "sameAs": [
            //        "https://atomico.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://atomico.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Sondo",
            //      "url": "https://www.sondo.com/",
            //      "@id": "PD0p2pDN3sZp4xmEwnqLgG",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/657402cb0b5bd81affed04933de948ec282ae065-435x94.svg",
            //      "sameAs": [
            //        "https://www.sondo.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.sondo.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Sten-Roger Karlsen",
            //      "url": "https://oiw.no/speaker/sten-roger-karlsen",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "29cfuvScEVHxUPeK7hJjCS",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/c8bb6aca853c32ec609abfc8e24ef4badbcdfa27-655x655.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Amanda Hultman",
            //      "url": "https://oiw.no/speaker/amanda-hultman1",
            //      "@type": "EducationGroup",
            //      "@id": "VJwDF6OhsypgL07DCOrNUB",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ae4f6290bbc49375c7f1c1448742a0af65212810-195x183.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Henrik Hatlebrekke",
            //      "url": "https://oiw.no/speaker/henrik-hatlebrekke",
            //      "@type": "EducationGroup",
            //      "@id": "29cfuvScEVHxUPeK7hK89Y",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/08828a1dd237e1f5bfea71b335304821b1b3c009-200x200.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "investinor24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Afterparty",
            //      "Networking"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Investinor Investors Reception - by invitation only Sending event 'Investinor Investors Reception - by invitation only' to importer
            //{
            //  "name": "Startuplab and SNÖ Ventures Invitational",
            //  "url": "https://oiw.no/event/invitational24",
            //  "startDate": "2024-09-25T15:00:00.000Z",
            //  "localStartDate": "2024-09-25T17:00",
            //  "endDate": "2024-09-25T18:30:00.000Z",
            //  "description": "A fun and informal evening for investors where you will meet the most interesting Norwegian tech startups for a friendly game of minigolf and top tier networking.",
            //  "location": {
            //    "name": "Oslo Camping",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9145869,
            //      "longitude": 10.7448016
            //    },
            //    "@type": "Place",
            //    "@id": "PygGVB9TYoPQnjIow9ienz",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Startuplab ",
            //      "url": "https://startuplab.no/",
            //      "@id": "hQLKYFGzcQ7wYQCuKl4G63",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/5f7bcedf075b07d0a82ba928349237be0a99bddd-1500x788.png",
            //      "sameAs": [
            //        "https://startuplab.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://startuplab.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "SNÖ Ventures",
            //      "url": "https://sno.vc/",
            //      "@id": "hQLKYFGzcQ7wYQCuKl4RPf",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/e8fbfd8fea3544e2abeeedf57104015806a7d2c4-1216x628.svg",
            //      "sameAs": [
            //        "https://sno.vc/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://sno.vc/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "invitational24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Investment",
            //      "Networking"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Startuplab and SNÖ Ventures Invitational Sending event 'Startuplab and SNÖ Ventures Invitational' to importer
            //{
            //  "name": "Pioneering good health with AI",
            //  "url": "https://oiw.no/event/pioneering-good-health-with-ai24",
            //  "startDate": "2024-09-26T13:00:00.000Z",
            //  "localStartDate": "2024-09-26T15:00",
            //  "endDate": "2024-09-26T15:00:00.000Z",
            //  "description": "",
            //  "location": {
            //    "name": "Polyteknisk Forening",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.915039,
            //      "longitude": 10.7369476
            //    },
            //    "@type": "Place",
            //    "@id": "0gWGhxa36zGzbzBhLbadAS",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Polyteknisk Forening",
            //      "url": "https://www.polyteknisk.no/",
            //      "@id": "0gWGhxa36zGzbzBhLbbNxR",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/94bb57a7e564f4b8ff8b08b783765e3ac96054fd-1080x1080.png",
            //      "sameAs": [
            //        "https://www.polyteknisk.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.polyteknisk.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Sharmini Alagaratnam",
            //      "url": "https://oiw.no/speaker/sharmini-alagaratnam",
            //      "description": "Sharmini Alagaratnam holds a PhD in Protein Engineering and an MSc in Biochemical Research. She serves as the Program Director of DNV\u0027s Healthcare Research Programme, which strategically focuses on ensuring AI assurance for healthcare. Additionally, she holds the position of Chair at the Norwegian Polytechnic Society\u0027s Digital Leadership Network.",
            //      "@type": "EducationGroup",
            //      "@id": "9ahf2G8xTqCQpwz3etAJbm",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/45f3db6776bfe58a172f7aeed55b90b1640e53ec-500x500.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Ishita Barua",
            //      "url": "https://oiw.no/speaker/ishita-barua",
            //      "description": "Ishita holds a Medical Doctor degree, a PhD in Medicine, and is a Health Economist. She is the Co-founder and Chief Medical Officer at Livv Health, a health-tech startup, and has previously worked as Lead AI in Healthcare at Deloitte.",
            //      "@type": "EducationGroup",
            //      "@id": "9ahf2G8xTqCQpwz3etDzOO",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/a946f9be1eef2a6e3d1c7b9f2dce12a75ed25649-590x590.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Ketil F.K Widerberg",
            //      "url": "https://oiw.no/speaker/ketil-f-widerberg",
            //      "description": "Ketil F. Widerberg holds an MBA and possesses a background in both the life science and software industries. He is the General Manager of Oslo Cancer Cluster, an oncology research and industry cluster.",
            //      "@type": "EducationGroup",
            //      "@id": "yyid1sjTjwIAZT5JGmwHjn",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/8a3e1e0797a7fe538616a9063b0ccd59f5506ca9-590x590.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Camilla Stoltenberg",
            //      "url": "https://oiw.no/speaker/camilla-stoltenberg",
            //      "description": "Camilla Stoltenberg is a Medical Doctor and holds a PhD in Epidemiology.  She was a former professor at the University of Bergen and headed the Norwegian Institute of Public Health through the Covid Pandemic. Camilla is currently the CEO of NORCE.",
            //      "@type": "EducationGroup",
            //      "@id": "9ahf2G8xTqCQpwz3etF3do",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/6c46e5b802e76e11cdd54268ef8da898019d0777-590x590.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Jan Christian Vestre",
            //      "url": "https://oiw.no/speaker/jan-christian-vestre",
            //      "description": "Jan Christian Vestre is a Norwegian business leader and politician for the Labour Party who has been serving as the Minister of Health in the Støre government since 2024. From 2021 to 2024, he was the Minister of Trade and Industry. Since 2023, he has also been the deputy leader of the Labour Party.",
            //      "@type": "EducationGroup",
            //      "@id": "Pn48h1OtVfcepeo3p8gEbr",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/c64fe540d61bd289591660713c11184caf93d691-590x590.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Mette Vågnes Eriksen",
            //      "url": "https://oiw.no/speaker/mette-vågnes-eriksen",
            //      "description": "Mette Vågnes Eriksen is a Norwegian Economist and holds a Master\u0027s in Economics. She has formerly worked as Director of Sustainability at DNV, been the VP of Corporate Responsibility at REC, and has many years of background in both Statkraft and SWECO. Since 2018, she has been the General Secretary of the Norwegian Polytechnic Society.",
            //      "@type": "EducationGroup",
            //      "@id": "1y9vrm2LhG44NjaC762gOf",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/405603284e2c02ca7941f44e68e385e1fe775822-590x590.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "pioneering-good-health-with-ai24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "HealthTech",
            //      "Networking",
            //      "Workshop"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Pioneering good health with AI Sending event 'Pioneering good health with AI' to importer
            //{
            //  "name": "The dos and don\u0027ts of startup branding with Strise.ai \u0026 Maki.vc",
            //  "url": "https://oiw.no/event/maki-vc24",
            //  "startDate": "2024-09-25T06:30:00.000Z",
            //  "localStartDate": "2024-09-25T08:30",
            //  "endDate": "2024-09-25T08:00:00.000Z",
            //  "description": "Seats fill up fast so sign up quickly!",
            //  "location": {
            //    "name": "Byens Tak",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 61.2833727,
            //      "longitude": 12.5663422
            //    },
            //    "@type": "Place",
            //    "@id": "dmhYqF2iO5Llcr6O4RUOHo",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Maki.vc",
            //      "url": "https://maki.vc/",
            //      "@id": "WbjlOM4Ar2clmGQeiwIDrV",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/03b794b331caaadf8351504792c68ae7762d815c-1319x628.png",
            //      "sameAs": [
            //        "https://maki.vc/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://maki.vc/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Strise",
            //      "url": "https://www.strise.ai/",
            //      "@id": "LxW2c7ek5iDOrc2j9LF6ju",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/76f6f16716efa88769270f6c93025c4799f58995-73x19.svg",
            //      "sameAs": [
            //        "https://www.strise.ai/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.strise.ai/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Venla Väänänen",
            //      "url": "https://oiw.no/speaker/venla-väänänen",
            //      "description": "Venla actively supports Maki’s portfolio companies in their growth and marketing operations. Before jumping into the world of venture capital, Venla gained strong expertise in marketing and communications on the other side of the table, leading marketing and communications for the health tech company Gubbe.",
            //      "@type": "EducationGroup",
            //      "@id": "jjBq4X6gdc6IuElj7iacY5",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/aedcebf727adf42e12404f52b5a1c9d93707ecca-4180x2787.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Marit Rødevand",
            //      "url": "https://oiw.no/speaker/marit-rødevand",
            //      "description": "Marit Rødevand is the Co-founder and CEO of Strise, an AML automation company. She has a master’s in Engineering Cybernetics from the Norwegian University of Science and Technology (NTNU). Marit is also the host of The Laundry, the podcast connecting anti-money laundering, compliance, and financial crime to the real world.",
            //      "@type": "EducationGroup",
            //      "@id": "LxW2c7ek5iDOrc2j9Irh9d",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/b31f75c2ebf075a212341395348eeed17e5498f8-400x400.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Lars Birkeland",
            //      "url": "https://oiw.no/speaker/lars-birkeland",
            //      "description": "Lars Birkeland is the Chief Marketing Officer at Strise and has played a pivotal role in shaping Strise’s marketing initiatives, driving brand growth, and expanding market reach. ",
            //      "@type": "EducationGroup",
            //      "@id": "jjBq4X6gdc6IuElj7ieFpn",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/0674a676aaa038e5ba7dfe54ab509fd879be46d7-400x400.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "maki-vc24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Scaling",
            //      "FinTech",
            //      "Fireside"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] The dos and don'ts of startup branding with Strise.ai & Maki.vc Sending event 'The dos and don'ts of startup branding with Strise.ai & Maki.vc' to importer
            //{
            //  "name": "Industrial Tech Breakfast",
            //  "url": "https://oiw.no/event/discover-norways-tech-heritage24",
            //  "startDate": "2024-09-25T06:00:00.000Z",
            //  "localStartDate": "2024-09-25T08:00",
            //  "endDate": "2024-09-25T08:30:00.000Z",
            //  "description": "Join us for breakfast and coffee to engage with fellow investors, gain inspiration, and discuss the latest updates from the field of industrial tech",
            //  "location": {
            //    "name": "Oslo Opera House, Kirsten Flagstads pl. 1 N-0150 Oslo",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9078346,
            //      "longitude": 10.7520679
            //    },
            //    "@type": "Place",
            //    "@id": "tpVQD7tR2tQiUXG3JvR7Zr",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "RunwayFBU",
            //      "url": "https://runwayfbu.com",
            //      "@id": "obc8VxsOhfAT1cAiOV7Fci",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/bb4c47500b53d89b202df24ffd76b67248afc186-3046x355.png",
            //      "sameAs": [
            //        "https://runwayfbu.com"
            //      ],
            //      "gogo": {
            //        "webpage": "https://runwayfbu.com"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Idekapital",
            //      "url": "https://idekapital.com",
            //      "@id": "AfyuFOmFmEYg7iijoF00nK",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/bed747300fef01614c9fd4f6e166d3ab02b8c179-7553x1292.png",
            //      "sameAs": [
            //        "https://idekapital.com"
            //      ],
            //      "gogo": {
            //        "webpage": "https://idekapital.com"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "NordicNinja ",
            //      "url": "https://nordicninja.com",
            //      "@id": "Yq34pjRdJp5sgmEOH8pRji",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/b08b71fdcaa9fadc911ba70158acac5d7bf40117-2311x360.png",
            //      "sameAs": [
            //        "https://nordicninja.com"
            //      ],
            //      "gogo": {
            //        "webpage": "https://nordicninja.com"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Benedicte Willumsen Grieg",
            //      "url": "https://oiw.no/speaker/benedicte-willumsen-grieg",
            //      "@type": "EducationGroup",
            //      "@id": "tpVQD7tR2tQiUXG3JvpIPr",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/7437920b736b1412fd16e215b688c5aadb2076ea-400x400.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Andreas Steinsvoll Proesch",
            //      "url": "https://oiw.no/speaker/andreas-steinsvoll-proesch",
            //      "description": "Vice President of Corporate Development at Cognite, and Head of the Aker AI Unit\nWorking to accelerate AI and digitalization across Industrial sectors\nSpent his career at the interface between critical industries and frontier technologies\nActive strategy and technology advisor across industrial domains",
            //      "@type": "EducationGroup",
            //      "@id": "PUn43gDAws84BhCBDSi2ql",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/e000d094432b3791aa5b6f995a4fd843d33c0881-679x475.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "discover-norways-tech-heritage24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Investment",
            //      "IndustrialTech",
            //      "Networking"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Industrial Tech Breakfast Sending event 'Industrial Tech Breakfast' to importer
            //{
            //  "name": "Alliance VC x Eight Roads Drinks",
            //  "url": "https://oiw.no/event/alliance-x-eight-roads-networking24",
            //  "startDate": "2024-09-24T16:00:00.000Z",
            //  "localStartDate": "2024-09-24T18:00",
            //  "endDate": "2024-09-24T20:00:00.000Z",
            //  "location": {
            //    "name": "Hammerhai",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9064141,
            //      "longitude": 10.7409407
            //    },
            //    "@type": "Place",
            //    "@id": "dfE6l672ApHzwUo2FtcJLp",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Eight Roads ",
            //      "url": "https://eightroads.com/en/",
            //      "@id": "dfE6l672ApHzwUo2Ftcdpk",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/fa46b86c9331d933dfc8bc97ffeb13a4d0717a05-2414x171.png",
            //      "sameAs": [
            //        "https://eightroads.com/en/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://eightroads.com/en/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Alliance VC",
            //      "url": "https://alliance.vc/",
            //      "@id": "xMTjXAOVWvUWxUHZPv9NN3",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/8ed6c351c89b52364d381239d409652f513adc34-6519x1315.png",
            //      "sameAs": [
            //        "https://alliance.vc/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://alliance.vc/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "alliance-x-eight-roads-networking24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Networking",
            //      "Afterparty",
            //      "Investment"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Alliance VC x Eight Roads Drinks Sending event 'Alliance VC x Eight Roads Drinks' to importer
            //{
            //  "name": "Welcome to the Team: Joining Forces with AI",
            //  "url": "https://oiw.no/event/accenture24",
            //  "startDate": "2024-09-25T15:00:00.000Z",
            //  "localStartDate": "2024-09-25T17:00",
            //  "endDate": "2024-09-25T18:00:00.000Z",
            //  "description": "Can Generative AI adapt and respond to human behavior so we accept them as team members? ",
            //  "location": {
            //    "name": "Accenture, Rådhusgata 27",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9111949,
            //      "longitude": 10.7314922
            //    },
            //    "@type": "Place",
            //    "@id": "EI0bD3Cm7qBjoA5xSWNAuF",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Accenture Song",
            //      "url": "https://www.accenture.com/no-en/about/accenture-song-index",
            //      "@id": "Ae7ZTd1CwzH5RnEGIvoswG",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/68ea71e91e4f2a5891c187136e99f0e5b0e0e566-2411x300.png",
            //      "sameAs": [
            //        "https://www.accenture.com/no-en/about/accenture-song-index"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.accenture.com/no-en/about/accenture-song-index"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Simli",
            //      "url": "https://www.simli.com/",
            //      "@id": "SDLUuTWhTFC5UwnLL4SsJm",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/be116a841e8a66be7e4726d3688e1963dae6805d-103x32.svg",
            //      "sameAs": [
            //        "https://www.simli.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.simli.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Østfold University College",
            //      "url": "https://www.hiof.no/english/",
            //      "@id": "5ObSRLPRVl3eQGU5fdY7PJ",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/91e6b833a39889adfc5007ccb7ef41c0741d2522-405x94.png",
            //      "sameAs": [
            //        "https://www.hiof.no/english/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.hiof.no/english/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Daniel Fraga",
            //      "url": "https://oiw.no/speaker/daniel-fraga",
            //      "description": "Daniel is a conversational AI specialist at Accenture Song. He is the author of  \"Ontological Design: Subject is Project\", where he argues for consciously using AI to transform our understanding of humans, organisations and society. His concepts have directly shaped transformative AI applications and human-centric organisational strategies.",
            //      "@type": "EducationGroup",
            //      "@id": "EI0bD3Cm7qBjoA5xSWOYoS",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/78247ace1cbac28795fc84efb4921357a8fcabae-1280x1280.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Eirik F. Kjærnli",
            //      "url": "https://oiw.no/speaker/eirik-f-kjærnli",
            //      "description": "Eirik is a software engineer at heart, but also part of Accenture\u0027s Nordic GenAI Center of Excellence. He is deeply invested in how we build, evaluate and deliver generative AI solutions across the Nordics, as well as understanding how GenAI will impact us moving forward.",
            //      "@type": "EducationGroup",
            //      "@id": "Ae7ZTd1CwzH5RnEGIvpfES",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/35f26054e9bbaa68751a2849270409fb5e305a1c-1280x1280.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Heidi Frost Eriksen",
            //      "url": "https://oiw.no/speaker/heidi-frost-eriksen",
            //      "description": "Serial entrepreneur excelling in building companies from scratch to global scale-ups. Formerly VP Sales \u0026 Marketing at Huddly (AI-powered cameras) and CEO \u0026 Co-founder at GrepS (SW engineering skill building). Now COO at Simli, building AI models to humanize AI interactions.",
            //      "@type": "EducationGroup",
            //      "@id": "VdKfDJLLsUwX1ZJhWuQqEs",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/c14c48da5c1aa7eb0933e2ff4e029d7e7c2e9458-1080x1080.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Marianne Jansson Bjerkman",
            //      "url": "https://oiw.no/speaker/marianne-jansson-bjerkman",
            //      "description": "Marianne is Head of Section for IT Development and leader of the AI Council at Østfold University College. She is also the former Cluster Manager for the Norwegian Business Cluster for Applied AI where she worked to establish collaborations and concepts across the innovation eco system to foster adoption of AI.",
            //      "@type": "EducationGroup",
            //      "@id": "MA7iDNa6ipGIAEfr27pTMI",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/e5ffc675f3b95bba16b02ee843936bf9d634e120-540x540.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Paria Tahaee",
            //      "url": "https://oiw.no/speaker/paria-tahaee",
            //      "description": "Paria is an AI enthusiast with a multidisciplinary background as an entrepreneur, IT engineer and program manager. She is passionate about exploring GenAI\u0027s potential to simplify and enhance daily life. Her focus is on integrating AI into workspaces to boost efficiency, productivity, and effectiveness.",
            //      "@type": "EducationGroup",
            //      "@id": "LxW2c7ek5iDOrc2j9V72ic",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/e4216a7a7e1d3154d04d9cb07f824fd5a8bc6405-1082x1080.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "accenture24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Creative Tech",
            //      "Networking",
            //      "Panel"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Welcome to the Team: Joining Forces with AI Sending event 'Welcome to the Team: Joining Forces with AI' to importer
            //{
            //  "name": "P@SHA TechConnect",
            //  "url": "https://oiw.no/event/pasha24",
            //  "startDate": "2024-09-24T16:00:00.000Z",
            //  "localStartDate": "2024-09-24T18:00",
            //  "endDate": "2024-09-24T19:00:00.000Z",
            //  "description": "We bring together the world’s greatest minds; from leading tech corporations, inspiring pioneers from global start-ups and venture capitalists with the means to make it all happen.",
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "P@SHA",
            //      "url": "https://www.pasha.org.pk/",
            //      "@id": "jjBq4X6gdc6IuElj7kRgbu",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/56db29b08843d4b3c1670d4bc1b75643cc3449a3-904x276.png",
            //      "sameAs": [
            //        "https://www.pasha.org.pk/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.pasha.org.pk/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "",
            //      "@id": "C6T6FiNuOtEsdSQR5iwtsK",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/efbbd62061f3733c7a1b1664f8f8779ec8ad4f3d-1917x363.png",
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "",
            //      "@id": "C6T6FiNuOtEsdSQR5ix4qI",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/fae463bcc2b49408c572a7930b2eb96921643898-332x104.svg",
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "SMB Norge",
            //      "@id": "JK7YPb0vmgWWnGtO1SMFeY",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/6a9a5233d889219dc4e46bf8fe96c792cb126acb-1271x179.png",
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Saadia Altaf Qazi",
            //      "url": "https://oiw.no/speaker/saadia-altaf-qazi",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "sNzj4K0edIxTY7alYqHHUr",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/99c38f302aae55f3c4a8e5c52efa95cec62288bb-450x450.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Jamil Goheer",
            //      "url": "https://oiw.no/speaker/jamil-goheer",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "GBgGvFlp21zYJWMNelCCRD",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/6a4cc633fe86d14e2ee22c6a6fd1748fe3a5b886-355x355.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Fredrik Syversen",
            //      "url": "https://oiw.no/speaker/fredrik-syversen",
            //      "@type": "EducationGroup",
            //      "@id": "C6T6FiNuOtEsdSQR5iysqw",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/83669cb7a1c32952a03656e50ebb32fde03d4d57-500x500.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Karl Anders Grønland",
            //      "url": "https://oiw.no/speaker/karl-anders-grønland",
            //      "@type": "EducationGroup",
            //      "@id": "GBgGvFlp21zYJWMNelHsuV",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/6d698e2fdb6807d03b044d064b60da10591e16d8-450x450.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "pasha24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Afterparty",
            //      "Networking"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] P@SHA TechConnect Sending event 'P@SHA TechConnect' to importer
            //{
            //  "name": "How culture will increase ESG engagement",
            //  "url": "https://oiw.no/event/culture-intelligence24",
            //  "startDate": "2024-09-24T14:00:00.000Z",
            //  "localStartDate": "2024-09-24T16:00",
            //  "endDate": "2024-09-24T15:30:00.000Z",
            //  "description": "",
            //  "location": {
            //    "name": "Tjuvholmen Works",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": " Tjuvholmen alle 3",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "@type": "Place",
            //    "@id": "7J4ckUujc0bnkW7tixIePb",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Culture Intelligence",
            //      "url": "https://cultureintelligence.io/",
            //      "@id": "BIkyAjLlDo6DdcZKCdKKbF",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/5ef5446aa6ba1fac5ba518c49992596858680d9f-999x350.png",
            //      "sameAs": [
            //        "https://cultureintelligence.io/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://cultureintelligence.io/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Tone Ringstad",
            //      "url": "https://oiw.no/speaker/tone-ringstad",
            //      "description": "Tone, CEO of Culture Intelligence, is a leader in values \u0026 cultural systems. Her work in leadership culture \u0026 transformation is published internationally. With experience in oil, shipping, \u0026 global leadership, she\u0027s a respected speaker on values \u0026 leadership.",
            //      "@type": "EducationGroup",
            //      "@id": "f9Yd1l6KnN6lP8ivy2eOXM",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/2d708d56bdba03648fe98d4407dc042e99fbbf22-640x640.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Janne Britt Saltkjel",
            //      "url": "https://oiw.no/speaker/janne-britt-saltkjel",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "f9Yd1l6KnN6lP8ivy2f1Qy",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/d9f9ea59d01bfbd8201fd4d13909e8a6dedccfc6-640x640.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "culture-intelligence24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Sustainability",
            //      "Seminar"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] How culture will increase ESG engagement Sending event 'How culture will increase ESG engagement' to importer
            //{
            //  "name": "London \u0026 Beyond - A UK market guide for Norwegian businesses",
            //  "url": "https://oiw.no/event/goodwille24",
            //  "startDate": "2024-09-24T14:00:00.000Z",
            //  "localStartDate": "2024-09-24T16:00",
            //  "endDate": "2024-09-24T16:30:00.000Z",
            //  "description": "TBC",
            //  "location": {
            //    "name": "British Embassy Residence",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": " Oslo",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "@type": "Place",
            //    "@id": "j0P7aBo0bkM6HAdsmjZ8uw",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Nordic Edge",
            //      "url": "https://nordicedge.org/",
            //      "@id": "3bSeV1kXTHmPOpL581qB8Y",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/58683b962f9a0236782385d8e9019c35a2588bf2-511x233.png",
            //      "sameAs": [
            //        "https://nordicedge.org/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://nordicedge.org/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Goodwille",
            //      "url": "https://goodwille.com/",
            //      "@id": "3bSeV1kXTHmPOpL581qBzq",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ee88783cde880aeb301951c494c402d2340cf6f9-2000x338.png",
            //      "sameAs": [
            //        "https://goodwille.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://goodwille.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Department for Business and Trade Norway",
            //      "url": "https://www.gov.uk/world/organisations/department-for-business-and-trade-norway",
            //      "@id": "PygGVB9TYoPQnjIowAXhQH",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/af773c5f1bfd29aff4fedaca2d56ffaee3a019e2-2181x1651.png",
            //      "sameAs": [
            //        "https://www.gov.uk/world/organisations/department-for-business-and-trade-norway"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.gov.uk/world/organisations/department-for-business-and-trade-norway"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Giles Chamberlin",
            //      "url": "https://oiw.no/speaker/giles-chamberlin",
            //      "@type": "EducationGroup",
            //      "@id": "hQO5G9JGI1thetXcx8NQHU",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/14151d422022d6fe15474f36cd4c0cdcc22b87a1-547x379.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Carl-Erik Michalsen Moberg",
            //      "url": "https://oiw.no/speaker/carl-erik-michalsen-moberg",
            //      "@type": "EducationGroup",
            //      "@id": "sjpLtqPJGYKEfVADBBUYNG",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/aec4f0501a88af02b11c32fb5d69fca66788f6b1-2441x1727.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Murray Callander",
            //      "url": "https://oiw.no/speaker/murray-callander",
            //      "@type": "EducationGroup",
            //      "@id": "NO7Mjjgfso2MMqbm3y6wHJ",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/814287a180a5b8bfcc1b4eb0fb8cf8be6b230c91-1000x1338.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Bente Loe",
            //      "url": "https://oiw.no/speaker/bente-loe",
            //      "@type": "EducationGroup",
            //      "@id": "Q01A6nB0EzzzgHsi7EZ6NV",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/20a64ea49d5c8084fe0a5f88a153d9b49c910dc2-2336x2570.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Robert Toms",
            //      "url": "https://oiw.no/speaker/robert-toms",
            //      "@type": "EducationGroup",
            //      "@id": "NO7Mjjgfso2MMqbm3y7RJW",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/6a29ae0552ae309eb569e37784f104437f570462-1827x2340.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Tzvete Doncheva",
            //      "url": "https://oiw.no/speaker/tzvete-doncheva",
            //      "@type": "EducationGroup",
            //      "@id": "NO7Mjjgfso2MMqbm3y7gj1",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/72b015f98d340192cf6c38964cdd1fb0ef96ea26-1980x1320.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Alex Rahaman",
            //      "url": "https://oiw.no/speaker/alex-rahaman",
            //      "@type": "EducationGroup",
            //      "@id": "sjpLtqPJGYKEfVADBBVeZo",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/c89fe07d81e25f07d17017d71238c30cfa1a1b3d-1606x2048.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "goodwille24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Networking",
            //      "Cleantech",
            //      "Seminar"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] London & Beyond - A UK market guide for Norwegian businesses Sending event 'London & Beyond - A UK market guide for Norwegian businesses' to importer
            //{
            //  "name": "Can you trust AI?  ",
            //  "url": "https://oiw.no/event/dnv-ventures",
            //  "startDate": "2024-09-23T11:00:00.000Z",
            //  "localStartDate": "2024-09-23T13:00",
            //  "endDate": "2024-09-23T13:00:00.000Z",
            //  "location": {
            //    "name": "Byens Tak ",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9145451,
            //      "longitude": 10.7470017
            //    },
            //    "@type": "Place",
            //    "@id": "O1SpQCBUX7vLH4JvdpwufA",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "DNV Ventures ",
            //      "url": "https://www.dnv.com/about/dnv-ventures/",
            //      "@id": "O1SpQCBUX7vLH4JvdpwznG",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/fd4839c4a86a0c01bebd8eddc10536062d4a5a4c-3205x1745.png",
            //      "sameAs": [
            //        "https://www.dnv.com/about/dnv-ventures/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.dnv.com/about/dnv-ventures/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "2021.AI",
            //      "url": "https://2021.ai/company/about-us/",
            //      "@id": "VJwDF6OhsypgL07DCTalUL",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/6f564fc0882e7f48f7c8362ba796aa3a0e0fb064-548x370.png",
            //      "sameAs": [
            //        "https://2021.ai/company/about-us/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://2021.ai/company/about-us/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Smartwatt",
            //      "url": "https://smartwatt.no/",
            //      "@id": "VBlNaGbxeXMiFaeMV6vgZu",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/8a9d52f8fe38b4f1a43cb800b259b8889cf51c8c-847x91.png",
            //      "sameAs": [
            //        "https://smartwatt.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://smartwatt.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Simula Consulting ",
            //      "url": "https://simula.consulting/",
            //      "@id": "JxpKUxUWSDPhcfPlpZi8OV",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/63bf84819e1e4c78f2d8cbb90a30754e9f6812c9-2140x632.png",
            //      "sameAs": [
            //        "https://simula.consulting/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://simula.consulting/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Toju Duke",
            //      "url": "https://oiw.no/speaker/toju-duke",
            //      "description": "Recognized as one of the top women in AI, Toju is author of Building Responsible AI Algorithms, previous Programme Manager Responsible AI at Google and CEO and founder of Diverse AI. Toju Duke is a thought leader and a driving force on building responsible AI. ",
            //      "@type": "EducationGroup",
            //      "@id": "29cfuvScEVHxUPeK7hwEWK",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/cc739d8b4b027ea6acb412dc087092c673d03dc7-1080x1080.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Helga M. Brogger",
            //      "url": "https://oiw.no/speaker/helga-m-brogger",
            //      "description": "Helga M. Brogger is a doctor of Medicine and AI researcher in DNV focused on AI and health. Helga is recognized as top 50 women in Tech in Norway and as board member multiple institutions in the intersect of AI and healthcare.  ",
            //      "@type": "EducationGroup",
            //      "@id": "VBlNaGbxeXMiFaeMV6tD6I",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/dc0b753dde099ee1fa8f529e1df50e67cafe0fad-1080x1080.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Nermina Ek",
            //      "url": "https://oiw.no/speaker/nermina-ek",
            //      "description": "Leader of DNV Ventures Trust in AI Venture fund investing in start-ups with frontier technologies and business models. ",
            //      "@type": "EducationGroup",
            //      "@id": "VBlNaGbxeXMiFaeMV6tXlK",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/5fa0319f20fe9ff3ce5a354cfdfe52a22eaa3db3-1080x1080.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Lars Petter Lunden",
            //      "url": "https://oiw.no/speaker/lars-petter-lunden",
            //      "description": "CEO and co-Founder of Smartwatt. An AI-first company deploying AI control systems for optimized energy consumption. ",
            //      "@type": "EducationGroup",
            //      "@id": "29cfuvScEVHxUPeK7hx5QS",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/5fd5f691e33f5d98dd0cc9c71333751cfb93c5e5-1080x1080.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Mikael Munck",
            //      "url": "https://oiw.no/speaker/mikael-munck",
            //      "description": "CEO and Founder of 2021.AI. A digital platform building trust in AI. ",
            //      "@type": "EducationGroup",
            //      "@id": "29cfuvScEVHxUPeK7hxBUa",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/1288a545466a9582da09f3dcb7cb8eaa34bd20d5-1080x1080.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Christian Agrell",
            //      "url": "https://oiw.no/speaker/christian-agrell",
            //      "description": "Christian Agrell has a PhD in mathematics and is a scientist at DNV leading DNVs Research on Assurance of AI focusing on acceptable risks of AI. ",
            //      "@type": "EducationGroup",
            //      "@id": "VJwDF6OhsypgL07DCTZDHt",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/b0f872fd9f689ef7b256bd638f0a137825c50b19-1080x1080.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Holger Hussmann",
            //      "url": "https://oiw.no/speaker/holger-hussman",
            //      "description": "Holger Hussman is a co-founder of Bluetooth and several other deep tech companies. Holger is currently CEO of Simula Consulting, part of Simula research, supporting industrial customers with Machine Learning and AI development.",
            //      "@type": "EducationGroup",
            //      "@id": "VJwDF6OhsypgL07DCTa45y",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/867cfac4c66ba616be3d1acf374489c6291114fd-1080x1080.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "dnv-ventures",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Investment",
            //      "Scaling",
            //      "Talk"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Can you trust AI?   Sending event 'Can you trust AI?  ' to importer
            //{
            //  "name": "Workshop: Building Speed \u0026 Scale: Designing Your Growth Machinery",
            //  "url": "https://oiw.no/event/innovation-norway24",
            //  "startDate": "2024-09-23T08:00:00.000Z",
            //  "localStartDate": "2024-09-23T08:00:00.000Z",
            //  "endDate": "2024-09-23T13:00:00.000Z",
            //  "description": "Apply to join our exclusive workshop during Oslo Innovation Week and gain concrete, operational tools to transform your company into an efficient, value-driving machine. ",
            //  "location": {
            //    "name": "T3, Tordenskiolds gate 3",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": " 0160 Oslo",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.913164,
            //      "longitude": 10.7336859
            //    },
            //    "@type": "Place",
            //    "@id": "998c9697-8d53-4c11-b713-3feded08613f",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Innovation Norway",
            //      "url": "https://en.innovasjonnorge.no/",
            //      "@id": "bbcbf613-3359-4f55-abbe-9b200f29dbb4",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ba263ea26bba3fce42df9fd9e6c3fa54b235e567-4481x1953.png",
            //      "sameAs": [
            //        "https://en.innovasjonnorge.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://en.innovasjonnorge.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Oslo Innovation Week",
            //      "url": "https://oiw.no/",
            //      "@id": "2fea029b-1150-40c1-a191-ebbb24a6910d",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/92eee2b3af2cf96baf3b994ca8c0214b70b0a6aa-3167x2001.png",
            //      "sameAs": [
            //        "https://oiw.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Ola Forsstrom-Olsson",
            //      "url": "https://oiw.no/speaker/ola-forsstrom-olsson",
            //      "description": "Ola has over ten years as an entrepreneur and eight as a professional handball player, including in the EHF Champions League with a top five team. Ola works for Innovation Norway in New York and has since early 2000s helped hundreds of companies scale internationally in a sustainable and financially stable way.\nWith degrees in Engineering Physics and Medicine from Lund University, he specialises in applied mathematics. Ola founded and led a venture-backed bioinformatics company, licensing its technology to a NASDAQ-listed firm in 2010. He\u0027s contributed to defining standards for protein data informatics in Nature Biotechnology. \n\"The large amounts of experiences and observations I have accumulated over the years, both in my own founder journey 2000 - 2010, but also from working with companies in all industries trying to scale, has allowed me to develop a way of thinking around what tends to transition a company from the tinkering phase (startup) to a potential scaling phase.\", \nOla Forsström-Olsson",
            //      "@type": "EducationGroup",
            //      "@id": "519210f0-da44-4a96-abe4-6a0e8aca8559",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/59949a1327ee6904fd6dcda2856023ab09ff4783-1998x1991.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Ravi Belani",
            //      "url": "https://oiw.no/speaker/ravi-belani1",
            //      "description": "Ravi Belani is the Founder and Managing Director of \nAlchemist Accelerator\n, a venture backed accelerator for enterprise startups. Ravi is also an Adjunct Lecturer at Stanford University, where he leads the Entrepreneurial Thought Leaders Seminar (the largest class on entrepreneurship at Stanford). He is an early Investor in Twitch / Justin.TV, Pubmatic, Rigetti, LaunchDarkly.",
            //      "@type": "EducationGroup",
            //      "@id": "1a767960-a83b-4754-8715-e8e9375df1f8",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/85aaf63b4ae84e295e0aaa274095e0bd5f1a506b-900x506.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "innovation-norway24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Scaling",
            //      "Workshop"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Workshop: Building Speed & Scale: Designing Your Growth Machinery Sending event 'Workshop: Building Speed & Scale: Designing Your Growth Machinery' to importer
            //{
            //  "name": "Official Opening Party! ",
            //  "url": "https://oiw.no/event/mesh-afterparty",
            //  "startDate": "2024-09-23T17:00:00.000Z",
            //  "localStartDate": "2024-09-23T17:00:00.000Z",
            //  "endDate": "2024-09-23T21:00:00.000Z",
            //  "description": "",
            //  "location": {
            //    "name": "Mesh Youngstorget",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9139676,
            //      "longitude": 10.7439046
            //    },
            //    "@type": "Place",
            //    "@id": "zEeIDkoVPW7SG8VDrkPpwP",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Mesh Community",
            //      "url": "https://meshcommunity.com/",
            //      "@id": "HJ5uo4LMcm2DGW55qrGO6E",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/d48bebc08c7a61f88361875a5e7c738f02ff8126-5178x1601.png",
            //      "sameAs": [
            //        "https://meshcommunity.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://meshcommunity.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "mesh-afterparty",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Community",
            //      "Networking"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Official Opening Party!  Sending event 'Official Opening Party! ' to importer
            //{
            //  "name": "Scaleup Brand building: Make people care about your product",
            //  "url": "https://oiw.no/event/mars-brand-workshop24",
            //  "startDate": "2024-09-23T06:30:00.000Z",
            //  "localStartDate": "2024-09-23T08:30",
            //  "endDate": "2024-09-23T08:30:00.000Z",
            //  "description": "The market is flooded with companies all claiming they\u0027ll change the way we work. With so much noise, it\u0027s tough for any product to stand out. In this talk, Monna and Bao will share how they help founders distill their value proposition to its very core. And by doing so, help elevate their image beyond that of an early-stage startup and into something impossible for customers, competitors, and investors to ignore.",
            //  "location": {
            //    "name": "Try",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": " 0157 Oslo",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9124694,
            //      "longitude": 10.7407586
            //    },
            //    "@type": "Place",
            //    "@id": "nIyegy3FVu4hKJCZdLzZku",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Mars Brand Strategy",
            //      "url": "https://mars.as/",
            //      "@id": "nIyegy3FVu4hKJCZdLzeFK",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/0d5f06cf9c8bfa3420446a028b949f75595681a9-4292x1918.png",
            //      "sameAs": [
            //        "https://mars.as/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://mars.as/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Umble",
            //      "url": "https://www.umble.no/",
            //      "@id": "nNlMDwdaFpeRBQSKRWjAGs",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/5240b62db5929560ef886b2bbe0b637248009a9c-151x42.svg",
            //      "sameAs": [
            //        "https://www.umble.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.umble.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Naer",
            //      "url": "https://naer.io/",
            //      "@id": "EID7htTIDezvC7ki0VWY4w",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/7e31fa8d9498f05095c610ce0fbbd7d413333671-2000x500.png",
            //      "sameAs": [
            //        "https://naer.io/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://naer.io/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Even Ifar Fossland",
            //      "url": "https://oiw.no/speaker/even-ifar-fossland",
            //      "@type": "EducationGroup",
            //      "@id": "udBHoLbJZaALmUA1TtbUMV",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/1020f0af89bed968e529728bb349941f57f9c7b7-1344x1004.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Bao Nguyen",
            //      "url": "https://oiw.no/speaker/bao-nguyen",
            //      "description": "Bao co-founded Umble, a design agency specializing in UX and branding for tech-scaleups. Over the past three years, he and his team have contributed to launching over 30 products, guiding founders through the most crucial design decisions.",
            //      "@type": "EducationGroup",
            //      "@id": "j0P7aBo0bkM6HAdsmjNfoW",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/bb232ce3bc94c7d4575af89c7c852c1118daf3f9-1365x2048.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Monna Nordhagen",
            //      "url": "https://oiw.no/speaker/monna-nordhagen",
            //      "description": "Monna is a business strategist, public speaker and method developer. The brand strategy\u0027s number one job is to redeem the business strategy and bring it to life in the organization. No one understands this interplay better than Monna.",
            //      "@type": "EducationGroup",
            //      "@id": "3bSeV1kXTHmPOpL581ghpu",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/96ef26ea04bcd67aa80a40c72a02091d0f4f18ab-1826x2737.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Sondre Kvam",
            //      "url": "https://oiw.no/speaker/sondre-kvam",
            //      "description": "Sondre is CEO and co-founder of Naer, a spatial collaboration platform for remote and hybrid teams. In 2023, Naer launched in partnership with Meta and has since been featured in The New York Times, Wall Street Journal, Fast Company, Wired, BBC, and Bloomberg, among others.",
            //      "@type": "EducationGroup",
            //      "@id": "nNlMDwdaFpeRBQSKRWiwSq",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/609aab2e954d4de3f9137dbb91748e5a5fe3cfaf-2000x1331.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Kirsti Rogne",
            //      "url": "https://oiw.no/speaker/kirsti-rogne",
            //      "description": "Kirsti is a true expert in brand identity, language and storytelling. Heavy industry or luxury hotel? In any case, Kirsti calls for a clear identity that people can identify with, a separate language and a story that people can cheer for.",
            //      "@type": "EducationGroup",
            //      "@id": "j0P7aBo0bkM6HAdsmjNyfs",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/010d8816b0faebc056c1f77a7d629b1673861812-1826x2737.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "mars-brand-workshop24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Scaling",
            //      "Networking",
            //      "Talk"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Scaleup Brand building: Make people care about your product Sending event 'Scaleup Brand building: Make people care about your product' to importer
            //{
            //  "name": "TOOL 5Y Launch: 500 Female Founders of Ocean \u0026 New Energy",
            //  "url": "https://oiw.no/event/tool24",
            //  "startDate": "2024-09-24T12:00:00.000Z",
            //  "localStartDate": "2024-09-24T14:00",
            //  "endDate": "2024-09-24T16:00:00.000Z",
            //  "description": "TOOL 5th Anniversary \u0026 Launch: Global 500 List of Female Founders in Ocean \u0026 New Energy",
            //  "location": {
            //    "name": "TheFactory - 7th Floor \u0026 Rooftop",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9120461,
            //      "longitude": 10.73275
            //    },
            //    "@type": "Place",
            //    "@id": "HuD07P45JTXE1iqgf22UGt",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "TOOL - The Ocean Opportunity Lab",
            //      "url": "https://toolspawn.com/",
            //      "@id": "2da20e9b-134c-4035-bcf9-d2a62e571edc",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/39d33c623f1645ece0a5932fe529281c9bda104d-2670x1100.png",
            //      "sameAs": [
            //        "https://toolspawn.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://toolspawn.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Feminvest",
            //      "url": "https://feminvest.se/en/",
            //      "@id": "2Zk6tWckGuE3g8LyMSvfQ7",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/b5f4bb51e62b14862cbcbb1e5802860c4c0770ca-416x121.png",
            //      "sameAs": [
            //        "https://feminvest.se/en/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://feminvest.se/en/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "The Conduit",
            //      "url": "https://oslo.theconduit.com/",
            //      "@id": "6yIAR7qIdwNVpxkGJpfzZa",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/1ff3b5c748a35bfa56abc502602edea5697da32e-2495x695.png",
            //      "sameAs": [
            //        "https://oslo.theconduit.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oslo.theconduit.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Birgit Liodden",
            //      "url": "https://oiw.no/speaker/birgit-liodden",
            //      "@type": "EducationGroup",
            //      "@id": "zEeIDkoVPW7SG8VDrcY4Fr",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/804b1a9880c98a099b556f37c5e6a267b47d03c1-1900x1268.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Dilek Ayhan",
            //      "url": "https://oiw.no/speaker/dilek-ayhan",
            //      "description": "Dilek is the President \u0026 Impact Lead (Founding CEO) of Conduit Oslo, former Deputy Minister of Trade and Industry. ",
            //      "@type": "EducationGroup",
            //      "@id": "1VsbhneYEYwg8EYOpcGwQQ",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/5838a8a87f58fe7815924c9174e82d92c5349f60-883x901.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Elin Kristensson",
            //      "url": "https://oiw.no/speaker/elin-kristensson",
            //      "description": "Elin is the MD of N-O-S, an industry-leading supplier to the international offshore wind industry and providing crew transfer vessels for the transport of personnel and equipment, with a fleet of 65+ vessels, employing 350 staff.\n\nElin is Chair of Swedish Shipowners Organisation (SRO), Board member of Danish Shipping \u0026 Donsö Shipping Meet.",
            //      "@type": "EducationGroup",
            //      "@id": "2Zk6tWckGuE3g8LyMSzY3U",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/926934c33ba022b56de6609547cd339791f9f24e-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Nina Jensen",
            //      "url": "https://oiw.no/speaker/nina-jensen",
            //      "description": "Nina is the CEO of REV Ocean, and former CEO of WWF Norway. Board member of KR Foundation, Project Energy Reimagined, Ocean Wise, HUB Ocean \u0026 Aker Carbon Capture. \n\nCo-initiator of the 6000 List, aiming to gather 6.000 female candidates for board roles in Norwegian companies.",
            //      "@type": "EducationGroup",
            //      "@id": "2Zk6tWckGuE3g8LyMT08wm",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/05833e01fe9af3c463db117a349aad4797940ef6-312x312.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Nito Simonsen",
            //      "url": "https://oiw.no/speaker/nito-simonsen",
            //      "description": "ClimatePoint enables investors, startups, and businesses to streamline impact data in line with global frameworks and EU regulations for communication and action. Before founding ClimatePoint, Nito held roles including Head of Digital at Arctic Securities, and Sr. Product Specialist at DNB Wealth Mgt.",
            //      "@type": "EducationGroup",
            //      "@id": "6yIAR7qIdwNVpxkGJpWm5A",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/996df32eb8d851882e2d5efbd6f397c4947da1aa-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Helene Ree Ruden",
            //      "url": "https://oiw.no/speaker/helene-ree-ruden",
            //      "description": "Ruden utilise data from the oil industry, developing circular solutions across energy and clean water. \n\nThis include award winning HEAT-technology for storage of industrial waste heat, and participation in EU Water4All Horizon project.",
            //      "@type": "EducationGroup",
            //      "@id": "1VsbhneYEYwg8EYOpcPfNu",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/298412711daf6ac8807d1b6de874547527e3f196-4015x2680.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Agnes Arnadottir",
            //      "url": "https://oiw.no/speaker/agnes-arnadottir",
            //      "description": "BRIM has developed and operate 5 silent, clean vessels for year-round tourism, with pioneering technology development radically extending reach, while protecting ocean life.",
            //      "@type": "EducationGroup",
            //      "@id": "2Zk6tWckGuE3g8LyMT0gda",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ed8acb415b3b2c52e83cb429b240d08b7140c1b9-1080x1080.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Kristin Lorange",
            //      "url": "https://oiw.no/speaker/kristin-lorange",
            //      "description": "Kristin has built up Norway´s leading floating sauna scaleup with KOK. They provide year-round experiences for locals and visitors, and almost doubled their revenue 3 years in a row.",
            //      "@type": "EducationGroup",
            //      "@id": "1VsbhneYEYwg8EYOpcQKpe",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/cc15198688d794739fd689dff65cfadc7b50b4e7-512x640.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Kristin Omholt-Jensen",
            //      "url": "https://oiw.no/speaker/kristin-omholt-jensen",
            //      "description": "With the vision to deliver maritime intelligence at your finger tips, Maritime Optima were recently included in Microsoft Azure Marketplace, with their subscription based solutions ShipAtlas and ShipIntel.\n\nOwner of KOJ Invest, and a serial entrepreneur, who previously founded Rendra (sold to JDM Technology Group) and I-Sea (sold to C-Map).",
            //      "@type": "EducationGroup",
            //      "@id": "1VsbhneYEYwg8EYOpcR5y6",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/9f9c03a6fc9bdc55ca30a4786b37f3ec0c86f59a-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Toril Leite",
            //      "url": "https://oiw.no/speaker/toril-leite",
            //      "description": "Serial entrepreneur, with an extensive leadership background from oil \u0026 gas, including SeaBird Exploration, CGG. Neomare joined Equinor/Techstar Energy Accelerator 2023, providing a resolution revolution in subsea imaging, producing ultra high definition 3D (UHR 3D) imaging of the seabed and below for a.o. offshore wind and carbon storage.\n",
            //      "@type": "EducationGroup",
            //      "@id": "2Zk6tWckGuE3g8LyMT4tAO",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/732e60b6876cdaae88381517c0a474791efe497f-256x256.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Sølvi Hjelmeland",
            //      "url": "https://oiw.no/speaker/sølvi-hjelmeland",
            //      "description": "Serial entrepreneur with background from media and telecom, who has developed a 100% eco \u0026 ocean life friendly pipe cleaning solution for maritime, with zero harmful components.",
            //      "@type": "EducationGroup",
            //      "@id": "6yIAR7qIdwNVpxkGJpdOBd",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/cefcfa7ac5ae390288ad0ce8285cc4c42454f89e-507x507.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Jeanette Grendahl",
            //      "url": "https://oiw.no/speaker/jeanette-grendahl",
            //      "description": "Jeanette comes from a family business background, and has dedicated her career to contribute across several diversity initiatives. She is a board member of Grendahl Holding family business, and serves on the advisory board of Cape Wine Import. Her background include Family Business Norway, Women Investor Network Norway and SHE Community.",
            //      "@type": "EducationGroup",
            //      "@id": "6yIAR7qIdwNVpxkGJpfDZs",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ce3aa81ad856df291ffd8d240138d852048d334b-310x310.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Sara Sorbet",
            //      "url": "https://oiw.no/speaker/sara-sorbet2",
            //      "description": "Sara is in charge of Feminvest´s EXPAND program, building a network for Nordic female founders to connect, scale and expand. Feminvest is Sweden´s largest community of female founders \u0026 investors, with more than 50.000 members.",
            //      "@type": "EducationGroup",
            //      "@id": "1VsbhneYEYwg8EYOpcXbR0",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/84470aa331ad8cfcdfeed33eeef35dc32e2d16e5-400x400.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "tool24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Blue Economy",
            //      "Cleantech",
            //      "Launch"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] TOOL 5Y Launch: 500 Female Founders of Ocean & New Energy Sending event 'TOOL 5Y Launch: 500 Female Founders of Ocean & New Energy' to importer
            //{
            //  "name": "100 Pitches - Finals at DNB NXT",
            //  "url": "https://oiw.no/event/100-pitches-finals24",
            //  "startDate": "2024-09-26T08:00:00.000Z",
            //  "localStartDate": "2024-09-26T10:00",
            //  "endDate": "2024-09-26T16:00:00.000Z",
            //  "description": "DNB NXT 2024 - Where ideas meet capital\n\nDNB NXT is Norway’s most important meeting place between entrepreneurs, investors and the established business community.",
            //  "location": {
            //    "name": "DNB NXT",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9075534,
            //      "longitude": 10.7573172
            //    },
            //    "@type": "Place",
            //    "@id": "bJU0rnud4tYDJnYMKHrmRo",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "DNB",
            //      "url": "https://www.dnb.no/",
            //      "@id": "gtrSyynP7P1x0DLKmoGZoV",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/9ec0566823128ba5353e554eadd1d16766a066a0-2560x1762.png",
            //      "sameAs": [
            //        "https://www.dnb.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.dnb.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Startuplab",
            //      "url": "https://startuplab.no/",
            //      "@id": "FNKpwkKRimvSChfWMspDDP",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/5f7bcedf075b07d0a82ba928349237be0a99bddd-1500x788.png",
            //      "sameAs": [
            //        "https://startuplab.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://startuplab.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Oslo Innovation Week",
            //      "url": "https://oiw.no/",
            //      "@id": "2fea029b-1150-40c1-a191-ebbb24a6910d",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/92eee2b3af2cf96baf3b994ca8c0214b70b0a6aa-3167x2001.png",
            //      "sameAs": [
            //        "https://oiw.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "100-pitches-finals24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Talent",
            //      "Investment",
            //      "Business Conference"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] 100 Pitches - Finals at DNB NXT Sending event '100 Pitches - Finals at DNB NXT' to importer
            //{
            //  "name": "How Intelligence and Tech Work Together to Navigate Threats | Mesh Community Stage",
            //  "url": "https://oiw.no/event/mesh-community-stage-open-horizon",
            //  "startDate": "2024-09-24T06:00:00.000Z",
            //  "localStartDate": "2024-09-24T08:00",
            //  "endDate": "2024-09-24T08:00:00.000Z",
            //  "description": "Founded by individuals with extensive intelligence experience, OpenHorizon aim to make companies thrive by understanding and mitigating security risks. Join us at Oslo Innovation Week to observe how and where threats of today operates, and how technology change the rules of play by putting companies in front of the threat actors.",
            //  "location": {
            //    "name": "Mesh Nationaltheatret",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9130126,
            //      "longitude": 10.7341709
            //    },
            //    "@type": "Place",
            //    "@id": "FyAHGzCGYvVljtOszk0rrs",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "OpenHorizon",
            //      "url": "https://www.openhorizon.no/",
            //      "@id": "yM8GruyLTF3p8YPOtsQQ1M",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/182aa6dc2899613f7480e7f455fd52a466d0daea-1011x415.png",
            //      "sameAs": [
            //        "https://www.openhorizon.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.openhorizon.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Magnus Fjetland",
            //      "url": "https://oiw.no/speaker/magnus-fjetland",
            //      "description": "Magnus is co-founder of OpenHorizon, with over 18 years of experience in intelligence. Previously served as Chief Product Officer, specialised in the field of analysis, particularly counterintelligence and -terrorism. As an risk expert while being the founder of a tech company, he is committed to utilising technology to drive innovation.",
            //      "@type": "EducationGroup",
            //      "@id": "C6T6FiNuOtEsdSQR5bWAwI",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/53d6e96b9dda62032702bdcf8a00ec4deda15fa0-3351x3468.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "mesh-community-stage-open-horizon",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Creative Tech",
            //      "Impact",
            //      "Community Stage"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] How Intelligence and Tech Work Together to Navigate Threats | Mesh Community Stage Sending event 'How Intelligence and Tech Work Together to Navigate Threats | Mesh Community Stage' to importer
            //{
            //  "name": "100 Pitches - Quarter Final",
            //  "url": "https://oiw.no/event/100-pitches-quarter-final24",
            //  "startDate": "2024-09-24T12:00:00.000Z",
            //  "localStartDate": "2024-09-24T14:00",
            //  "endDate": "2024-09-24T15:00:00.000Z",
            //  "description": "100 PITCHES - Norway’s largest pitching competition for investor-ready startups\n\nFor startups: Join the competition here https://www.100.startuplab.no/\nFor audience: Join the Quarter Final here https://startuplabno.typeform.com/to/qra5ic9j",
            //  "location": {
            //    "name": "Oslo Science Park - Startuplab",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9419998,
            //      "longitude": 10.7143606
            //    },
            //    "@type": "Place",
            //    "@id": "bJU0rnud4tYDJnYMKHoKco",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "DNB",
            //      "url": "https://www.dnb.no/",
            //      "@id": "gtrSyynP7P1x0DLKmoFUs0",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/9ec0566823128ba5353e554eadd1d16766a066a0-2560x1762.png",
            //      "sameAs": [
            //        "https://www.dnb.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.dnb.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Startuplab",
            //      "url": "https://startuplab.no/",
            //      "@id": "bJU0rnud4tYDJnYMKHpS5J",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/5f7bcedf075b07d0a82ba928349237be0a99bddd-1500x788.png",
            //      "sameAs": [
            //        "https://startuplab.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://startuplab.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Oslo Innovation Week",
            //      "url": "https://oiw.no/",
            //      "@id": "2fea029b-1150-40c1-a191-ebbb24a6910d",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/92eee2b3af2cf96baf3b994ca8c0214b70b0a6aa-3167x2001.png",
            //      "sameAs": [
            //        "https://oiw.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "100-pitches-quarter-final24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Talent",
            //      "Investment",
            //      "Pitch"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] 100 Pitches - Quarter Final Sending event '100 Pitches - Quarter Final' to importer
            //{
            //  "name": "👟  Bruce Studios x Mesh: Skill Enhancement Run ",
            //  "url": "https://oiw.no/event/bruce-x-mesh-skill-enhancement-run",
            //  "startDate": "2024-09-24T05:30:00.000Z",
            //  "localStartDate": "2024-09-24T07:30",
            //  "endDate": "2024-09-24T06:15:00.000Z",
            //  "location": {
            //    "name": "Mesh Nationaltheatret",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9130126,
            //      "longitude": 10.7341709
            //    },
            //    "@type": "Place",
            //    "@id": "FyAHGzCGYvVljtOszk0rrs",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Bruce Studios",
            //      "url": "https://www.brucestudios.com",
            //      "@id": "NO7Mjjgfso2MMqbm3xTOJw",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/007e4eaa33d6fb7954b4300689af8ef79744ce11-400x400.png",
            //      "sameAs": [
            //        "https://www.brucestudios.com"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.brucestudios.com"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "bruce-x-mesh-skill-enhancement-run",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Fitness \u0026 Wellbeing",
            //      "Sport"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] 👟  Bruce Studios x Mesh: Skill Enhancement Run  Sending event '👟  Bruce Studios x Mesh: Skill Enhancement Run ' to importer
            //{
            //  "name": "Impact Breakfast with Carbon Centrum",
            //  "url": "https://oiw.no/event/carbon-centrum24",
            //  "startDate": "2024-09-24T06:30:00.000Z",
            //  "localStartDate": "2024-09-24T08:30",
            //  "endDate": "2024-09-24T08:00:00.000Z",
            //  "description": "Come and join us at Impact StartUp Space for a fresh breakfast \u0026 Learn how to measure your CarbonID.",
            //  "location": {
            //    "name": "Impact StartUp",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.911285,
            //      "longitude": 10.7417298
            //    },
            //    "@type": "Place",
            //    "@id": "oBQVrB4gs8hllVoXT28ADw",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Carbon Centrum",
            //      "url": "https://www.linkedin.com/company/carboncentrum",
            //      "@id": "PygGVB9TYoPQnjIowCIYTE",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/312e465bbfc2ba0294af53060567de53bf533c28-1926x692.png",
            //      "sameAs": [
            //        "https://www.linkedin.com/company/carboncentrum"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.linkedin.com/company/carboncentrum"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Impact Startup",
            //      "url": "https://www.south-zero.impactstartup.no/",
            //      "@id": "fwluKSLh9gfPaX1JBrpr7G",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/dbd1fb8864c36871c8b8699df44fb7daa27b0c47-447x236.png",
            //      "sameAs": [
            //        "https://www.south-zero.impactstartup.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.south-zero.impactstartup.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Cagri Selcuklu",
            //      "url": "https://oiw.no/speaker/cagri-selcuklu",
            //      "description": "Multidisciplinary with OEM, Start-up, Volunteer \u0026 Board experience. Skilled in Design Thinking, Product \u0026 Project Management with Urban Mobility Focus.\nHe founded another startup and built all the way to exit. Now with his previous co-founders, they are building the future of CarbonID for individuals.",
            //      "@type": "EducationGroup",
            //      "@id": "fwluKSLh9gfPaX1JBrq2AG",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/c9ff51b0f70de5929ff2e984e1737194c65175e8-850x850.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Marte Sootholtet",
            //      "url": "https://oiw.no/speaker/marte-sootholtet",
            //      "description": "CEO at Impact StartUp Norge. Accelerator for entrepreneurs that change the way we do business. People and planet first.",
            //      "@type": "EducationGroup",
            //      "@id": "ia7ghcj6Cj8sG7Ap3y9HVu",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/766cedbae36a3e0e14d6b3a6a7e51f9ce9f5e187-2668x4000.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "carbon-centrum24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Impact",
            //      "Networking",
            //      "Fireside"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Impact Breakfast with Carbon Centrum Sending event 'Impact Breakfast with Carbon Centrum' to importer
            //{
            //  "name": "Unleashed – a Corporate Innovation Summit by Reodor Studios",
            //  "url": "https://oiw.no/event/reodor-event24",
            //  "startDate": "2024-09-26T09:30:00.000Z",
            //  "localStartDate": "2024-09-26T11:30",
            //  "endDate": "2024-09-26T14:00:00.000Z",
            //  "description": "https://www.linkedin.com/company/reodor/",
            //  "location": {
            //    "name": "Reodor Studios, Torggata 11, 0181 Oslo ",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": " 0181 Oslo",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9159537,
            //      "longitude": 10.7447528
            //    },
            //    "@type": "Place",
            //    "@id": "j0P7aBo0bkM6HAdsmizu38",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Reodor Studios",
            //      "url": "https://www.reodorstudios.com/",
            //      "@id": "nNlMDwdaFpeRBQSKRWW9Tk",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/d46123af2ee814370b55f766f8c754b6ca8741ed-999x117.svg",
            //      "sameAs": [
            //        "https://www.reodorstudios.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.reodorstudios.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Tellef Thorleifsson",
            //      "url": "https://oiw.no/speaker/tellef-thorleifsson",
            //      "description": "Tellef Thorleifsson has been Norfund\u0027s CEO since autumn 2018. Before this, he co-founded and led Northzone, growing it into a top international venture fund that raised over EUR 1.5 billion and invested in 130+ companies. He also co-founded the Voxtra Foundation, focusing on agribusiness in East Africa, and has held several directorships.",
            //      "@type": "EducationGroup",
            //      "@id": "brPasVeoHS59tFoOaQH1wN",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/cd37c40642b851f16553a5972acd39b0640828f7-708x1024.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Nicolai Møller",
            //      "url": "https://oiw.no/speaker/nicolai-møller",
            //      "description": "Meet Nicolai, the Head of Banking and Savings at Bulder! He co-founded the Volum conference and has become a digital banking expert. Bulder has revolutionized banking with its app-only experience, and this innovative approach meets the rising demand for digital banking solutions that offer convenience, speed, and user-friendly interfaces.",
            //      "@type": "EducationGroup",
            //      "@id": "7HzS7L2FZf6ukSalMJeDoz",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/2529f2b819e2d676ec9b75578699be67b18f0f4e-451x451.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Ragnhild Pettersen",
            //      "url": "https://oiw.no/speaker/nicolai-malling",
            //      "description": "Ragnhild Pettersen is the Managing Director at Steddy Norge, a venture launched by Mestergruppen, focused on making renovation and refurbishment seamless and sustainable. Steddy simplifies the entire process by connecting clients with experts, centralizing all project details online, and ensuring transparency and quality with no hidden costs.",
            //      "@type": "EducationGroup",
            //      "@id": "brPasVeoHS59tFoOaQJinS",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/d0d7a62b4556a4d1de4af417c56f31bb6f5fd29d-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Kjell Erik Ollendorff Lien",
            //      "url": "https://oiw.no/speaker/kjell-erik-ollendorff-lien-",
            //      "description": "Kjell Erik has extensive experience in the energy sector. Fremby delivers a cutting-edge, user-friendly solution that provides seamless, data-driven insights into machinery fleets in construction projects. Kjell Erik\u0027s command center buzzes with real-time data and forecasts, empowering the team to anticipate and prevent digital crises. ",
            //      "@type": "EducationGroup",
            //      "@id": "27jkfP9EFXFTG07fKcXjKO",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/5897ead998bcd905bf0525a38300e7dde9a98317-451x451.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Christine Lundberg Larsen",
            //      "url": "https://oiw.no/speaker/christine-lundberg-larsen",
            //      "description": "Christine Lundberg Larsen is the Managing Director at Amesto Footprint Amesto - a digital solution designed to simplify sustainability efforts. It helps businesses identify relevant laws, measure and report data, and transform insights into performance gains. Supported by expert advisors, it ensures compliance and drives sustainable growth.",
            //      "@type": "EducationGroup",
            //      "@id": "27jkfP9EFXFTG07fKcY8f2",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/395d5d91ca9bce6e5da8328e8766a00bd80635a9-451x451.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Louise Korsbæk Sondrup",
            //      "url": "https://oiw.no/speaker/louise-korsbæk-sondrup",
            //      "description": "Meet Louise Sondrup, a natural changemaker with a decade-long track record in driving innovation and growth. Formerly the Director of Innovation at Eviny, Louise is now channeling her passion for transformation into her own venture, helping companies accelerate commercial and business development for a greener future. ",
            //      "@type": "EducationGroup",
            //      "@id": "27jkfP9EFXFTG07fKcYCAm",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/4dd1b60f14c90160e2c4a3879e036778ec510149-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Ane Furu",
            //      "url": "https://oiw.no/speaker/ane-furu",
            //      "description": "Ane Furu is a dynamic innovation leader, currently driving global product strategy as VP of Product Management at Element Logic. She has a track record of scaling tech platforms across Europe as COO of Casi and spearheaded new mobility solutions at Møller Mobility Group. Ane also chairs Reodor Studios, leading ventures that shape the future of tech",
            //      "@type": "EducationGroup",
            //      "@id": "7HzS7L2FZf6ukSalMJfFBd",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/9a63849cf4db7a6912e67319d7430c1a0edbd801-451x451.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Arild Spandow",
            //      "url": "https://oiw.no/speaker/arild-spandow",
            //      "description": "Arild Spandow is the founder and CEO of Amesto Group and part owner of Spabogruppen. A passionate advocate for exponential technology and value-based leadership, he has spearheaded Amesto’s intrapreneur program, fostering new ventures like Meet Dottie and Aprila Bank. Fun fact: he’s also an accomplished drummer.",
            //      "@type": "EducationGroup",
            //      "@id": "brPasVeoHS59tFoOaQKbED",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/9e745100cbd72f4666480358fb09232a767b2972-800x800.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Murshid Ali",
            //      "url": "https://oiw.no/speaker/murshid-ali",
            //      "description": "Dr. Murshid is a leading tech entrepreneur having founded Skyfri, Norsk Renewables, and Huddlestock, each scaling to billion-kroner valuations. He serves on the board of Nysnø, and has invested in over 40 startups across Europe. Recently, he authored Å gjøre det umulige, detailing his journey in building groundbreaking tech companies.",
            //      "@type": "EducationGroup",
            //      "@id": "brPasVeoHS59tFoOaQLPrF",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/089d8f67b1c92fd03539af10c105a915bd8b9f99-800x800.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Terje Borkenhagen",
            //      "url": "https://oiw.no/speaker/terje-borkenhagen",
            //      "description": "Terje Borkenhagen is the CEO of Enny, a solar energy startup launched by OBOS and Hafslund. Enny drives sustainable growth by aiming to install solar panels for 10,000 homes annually. As the company expands, it champions a future of locally produced and shared energy, reflecting a strong commitment to sustainability.",
            //      "@type": "EducationGroup",
            //      "@id": "SB7jKuT7rqUsI4FhRv3PLt",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/2e809323943cbca4009c4c4358b4edc286d73ebb-800x800.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Johann Olav Koss",
            //      "url": "https://oiw.no/speaker/johann-olav-koss",
            //      "description": "Johann Olav Koss is Norway\u0027s speed skating legend and four-time Olympic gold medalist. He is the founder of Right To Play, an organization using sports to empower children in the world’s most disadvantaged areas, with 35 000 volunteers engaging over two million children weekly. Did you know that Koss also holds a degree in medicine?",
            //      "@type": "EducationGroup",
            //      "@id": "LxW2c7ek5iDOrc2j9P5AmK",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/96c4589bcd673719757061f73cfa7d2fae329a39-451x451.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Jørgen Elton Nilsen",
            //      "url": "https://oiw.no/speaker/jørgen-elton-nilsen",
            //      "description": "Meet Jørgen Elton, CEO of Elton Mobility, where he’s revolutionizing EV charging by uniting all networks on one seamless platform. Jørgen’s tech journey began at 15, and he has since launched multiple platforms, including VG Lab, where Elton Mobility was born. What started as an MVP now generates tens of millions in revenue. ",
            //      "@type": "EducationGroup",
            //      "@id": "jjBq4X6gdc6IuElj7pLSNj",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/30e2be26fd0a53cfdea809ea68ffabb03c8446f8-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Winta Negassi",
            //      "url": "https://oiw.no/speaker/winta-negassi1",
            //      "description": "Winta Negassi is an accomplished HR leader with a unique career journey. From touring the world as an R\u0026B artist to working at the UN, she climbed the ranks to lead HR at Warner Bros. Discovery in the Nordics. Now, as Head of HR for Northern Europe at Google, Winta shapes top-tier work cultures with a global perspective and a drive for excellence.",
            //      "@type": "EducationGroup",
            //      "@id": "jjBq4X6gdc6IuElj7pLadr",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/f1760957ce95d7beec4b4a818830db4de89ad373-451x451.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Martin Sommerseth",
            //      "url": "https://oiw.no/speaker/martin-sommerseth",
            //      "description": "Resolve is the AI pioneer focused on delivering high-performance, easy-to-integrate AI APIs that drive automation in WFM, Payroll, and ERP. Martin co-founded Resolve during his final year at NTNU when Visma approached him and his classmates to lead their AI initiative. He became Managing Director when Resolve became its own business unit in 2023.",
            //      "@type": "EducationGroup",
            //      "@id": "jjBq4X6gdc6IuElj7pLiqw",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/652be7b0d94e20510cff029ebd54e09de31027ef-800x800.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Ellen Lidgren",
            //      "url": "https://oiw.no/speaker/ellen-lidgren",
            //      "description": "Ellen has over 16 years of experience in design, business development, and digital product development, and has worked with companies like Gjensidige, Ikea, Aker BP, and Statnett. Previously at EGGS Design, Volue, and Statkraft, Ellen focused on sustainable innovation and now leads Reodor in shaping Nordic industries\u0027 future.",
            //      "@type": "EducationGroup",
            //      "@id": "v3431n7IAaIx7DOZmWamjW",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/357865a0b7263922fba0c97f93e92a5a6f0ff3de-451x451.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Nassir Achour",
            //      "url": "https://oiw.no/speaker/nassir-achour",
            //      "description": "Nassir Achour is Group CEO and co-founder of Reodor Studios, a leading corporate venture and innovation studio in Norway. With 15+ years of experience as a serial entrepreneur, he has worked on self-founded startups and corporate innovation projects with top-tier companies such as Aker, DNV, Entra, Fortum, Møller Mobility Group and Posten.",
            //      "@type": "EducationGroup",
            //      "@id": "v3431n7IAaIx7DOZmWb7lY",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/76770f65893e2f3d230beb7180b002b090bfdbe8-451x451.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "reodor-event24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Scaling",
            //      "Community",
            //      "Summit"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Unleashed – a Corporate Innovation Summit by Reodor Studios Sending event 'Unleashed – a Corporate Innovation Summit by Reodor Studios' to importer
            //{
            //  "name": "LEaD! - Listen, Evolve, and Dare!",
            //  "url": "https://oiw.no/event/lead-listen-evolve-and-dare",
            //  "startDate": "2024-09-26T15:00:00.000Z",
            //  "localStartDate": "2024-09-26T17:00",
            //  "endDate": "2024-09-26T17:00:00.000Z",
            //  "description": "https://www.instagram.com/listenevolveanddare/",
            //  "location": {
            //    "name": "Chateau Neuf",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9323409,
            //      "longitude": 10.7100237
            //    },
            //    "@type": "Place",
            //    "@id": "yHoLspzQ0JQW6PP7d81fXr",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Norwegian Hindu Students\u0027 Forum",
            //      "url": "https://www.instagram.com/nhsf_norge/",
            //      "@id": "yHoLspzQ0JQW6PP7d81jeS",
            //      "sameAs": [
            //        "https://www.instagram.com/nhsf_norge/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.instagram.com/nhsf_norge/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Start Oslo",
            //      "url": "https://www.startoslo.no",
            //      "@id": "yHoLspzQ0JQW6PP7d81pFd",
            //      "sameAs": [
            //        "https://www.startoslo.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.startoslo.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Natalia Pavlovska",
            //      "url": "https://oiw.no/speaker/natalia-pavlovska",
            //      "description": "Natalia is a social entrepreneur, founder and CEO of WomanUp Slovakia, a community with over 10,000 members that connects and supports female entrepreneurs across CEE. Natalia is a Forbes 30 under 30 honoree, received a presidential scholarship from BI Norwegian Business School and has spoken at numerous European conferences.",
            //      "@type": "EducationGroup",
            //      "@id": "NO7Mjjgfso2MMqbm3fTbEw",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/5bd7d2db1b7db5b3a755eed42b3465371d43404f-1927x2560.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "lead-listen-evolve-and-dare",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Networking",
            //      "Scaling",
            //      "Panel"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] LEaD! - Listen, Evolve, and Dare! Sending event 'LEaD! - Listen, Evolve, and Dare!' to importer
            //{
            //  "name": "Proptech Summit",
            //  "url": "https://oiw.no/event/proptech-summit24",
            //  "startDate": "2024-09-25T06:00:00.000Z",
            //  "localStartDate": "2024-09-25T08:00",
            //  "endDate": "2024-09-25T14:30:00.000Z",
            //  "description": "https://www.linkedin.com/company/proptech-norway/",
            //  "location": {
            //    "name": "Mesh Youngstorget",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "@type": "Place",
            //    "@id": "3bSeV1kXTHmPOpL580Kere",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Proptech Norway",
            //      "url": "https://www.proptechnorway.co/",
            //      "@id": "udBHoLbJZaALmUA1TrBBej",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/4fdbc5b7137eb4b5c9fdffbe7b6276a711e275fa-577x229.png",
            //      "sameAs": [
            //        "https://www.proptechnorway.co/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.proptechnorway.co/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Antony Slumbers",
            //      "url": "https://oiw.no/speaker/antony-slumbers",
            //      "description": "Antony, a renowned proptech expert, is a speaker, advisor, and writer. With a background in founding and exiting proptech companies, he now consults on transformation, technology, and innovation strategies for real estate. Antony educates on AI\u0027s transformative potential in real estate, focusing on the future of work, cities, and the built environm",
            //      "@type": "EducationGroup",
            //      "@id": "29cfuvScEVHxUPeK7iJ7aa",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/fe5aebdf4ae1b17d6cadc268513d58553ecab74b-640x640.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Nikki Greenberg",
            //      "url": "https://oiw.no/speaker/nikki-greenberg",
            //      "description": "Nikki explores how embracing emerging technologies like robotics, AI, and 5G is essential for leading industries today. She reveals how tech-enabled organizations are a present necessity, not a future concept. Nikki guides leaders in reimagining their businesses to align with the increasingly digital world in which we live, work, and communicate.",
            //      "@type": "EducationGroup",
            //      "@id": "29cfuvScEVHxUPeK7iJSzg",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/b09e3eb7773b866ebc964080d107dc4117e8d399-640x640.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Kristin Berg",
            //      "url": "https://oiw.no/speaker/kristin-berg",
            //      "description": "What is good? What is bad? And how can we get better? Kristin Berg has a good vantage point from which to talk about how quality data, established benchmarks, and a disruptive mindset are the keys to change. ",
            //      "@type": "EducationGroup",
            //      "@id": "Kr5dD2L5cV66ZmRYNvc2MU",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/fcd0c36732bd903ba403917477df0d46e6eeca74-750x721.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Daniel Butenschøn",
            //      "url": "https://oiw.no/speaker/daniel-butenschøn",
            //      "description": "Previously an investigative journalist and editor, Daniel has been CEO of Proptech Norway since it became a professional organization in 2020.",
            //      "@type": "EducationGroup",
            //      "@id": "Kr5dD2L5cV66ZmRYNvc5DC",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/92f3d48e0c75487e550421100d1752f57ed8732e-1228x1125.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "proptech-summit24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "PropTech",
            //      "Summit"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Proptech Summit Sending event 'Proptech Summit' to importer
            //{
            //  "name": "From Material Girl to Material World: Pioneering Textiles",
            //  "url": "https://oiw.no/event/nfta24",
            //  "startDate": "2024-09-24T08:00:00.000Z",
            //  "localStartDate": "2024-09-24T10:00",
            //  "endDate": "2024-09-24T14:00:00.000Z",
            //  "description": "NF\u0026TA will bring together experts from Finland, Italy, Germany, and Norway to explore the latest innovations in textile recycling and new fiber technologies on the 24th of September. Participants will have the opportunity to gain new insights, engage in networking, and learn about pioneering advancements from different countries in the field of sustainable textiles. Registration will soon open.",
            //  "location": {
            //    "name": "Ingensteds",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9199776,
            //      "longitude": 10.7501704
            //    },
            //    "@type": "Place",
            //    "@id": "r7AVkNBCGkr1NZw97y4acZ",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Business Finland",
            //      "url": "https://www.businessfinland.com/",
            //      "@id": "r7AVkNBCGkr1NZw97y9uiN",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/f316a1a9a843ce57b2aff9631422c34267354b6c-1193x508.png",
            //      "sameAs": [
            //        "https://www.businessfinland.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.businessfinland.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Embassy of Finland",
            //      "url": "https://um.fi/finland-s-representation-abroad-by-country/-/asset_publisher/dCMOY7lDMXLf/contactInfoOrganization/id/122089",
            //      "@id": "PygGVB9TYoPQnjIow7ALDw",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/c44be92e63cc5edaf09760f3897cf31667ea3239-1250x833.png",
            //      "sameAs": [
            //        "https://um.fi/finland-s-representation-abroad-by-country/-/asset_publisher/dCMOY7lDMXLf/contactInfoOrganization/id/122089"
            //      ],
            //      "gogo": {
            //        "webpage": "https://um.fi/finland-s-representation-abroad-by-country/-/asset_publisher/dCMOY7lDMXLf/contactInfoOrganization/id/122089"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "NF\u0026TA",
            //      "url": "https://www.nfta.no/",
            //      "@id": "WbjlOM4Ar2clmGQeiv48v3",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/54a6a83aa0c50eb6ac9b665135591c1224bdd280-3680x947.png",
            //      "sameAs": [
            //        "https://www.nfta.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.nfta.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "German-Norwegian Chamber of Commerce",
            //      "url": "https://norwegen.ahk.de/no",
            //      "@id": "r7AVkNBCGkr1NZw97yADSx",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/dd80201cf3beaf3a50aea24b00ff09764c688323-1596x386.jpg",
            //      "sameAs": [
            //        "https://norwegen.ahk.de/no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://norwegen.ahk.de/no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Embassy of Italy",
            //      "url": "https://amboslo.esteri.it/en/",
            //      "@id": "PygGVB9TYoPQnjIow7Ba5h",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ca814330d313edeed11d036575e504a5b276a290-417x292.png",
            //      "sameAs": [
            //        "https://amboslo.esteri.it/en/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://amboslo.esteri.it/en/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Embassy of Italy",
            //      "url": "https://www.ice.it/en/",
            //      "@id": "75hF6u0k3iHYTKl66Pdrgg",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/e97d027555823e40b7fb142c678ccb960132ba8e-1036x668.png",
            //      "sameAs": [
            //        "https://www.ice.it/en/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.ice.it/en/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Confederation of the German Textile and Fashion Industry – Textil + Mode e. V.",
            //      "url": "https://textil-mode.de/en/",
            //      "@id": "LMDtzdFP0V5rPqBZv1UmSx",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/6fb9410b960bb00f4ed3c53fa45ea6e3aa6e6c56-417x292.png",
            //      "sameAs": [
            //        "https://textil-mode.de/en/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://textil-mode.de/en/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Marco Penazzi",
            //      "url": "https://oiw.no/speaker/marco-penazzi-",
            //      "description": "Marco Penazzi  is Sales Manager with deep expertise on responsible social change, the innovative use of high-quality materials, and social innovation. As Sales Manager for the Italian social enterprise Quid, he leads B2B Sales Department and Product Development Department.",
            //      "@type": "EducationGroup",
            //      "@id": "jjBq4X6gdc6IuElj7icoRp",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/53ef348f91c4d2175a6969ebd5d750fc2d3436ed-1250x1250.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Jonas Stracke",
            //      "url": "https://oiw.no/speaker/jonas-stracke",
            //      "description": "Jonas Stracke is a textile engineer and Head of the Circular Economy and Resource Efficiency Department at the Confederation of the German Textile and Fashion industry. He works very closely on the practical and innovative transition between the textile and clothing industry and (textile) waste industry.",
            //      "@type": "EducationGroup",
            //      "@id": "LxW2c7ek5iDOrc2j9Ityq4",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/97ed524dc5532bd8970bd8bcef8f9528cdcf8be7-1250x1250.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Fabio Tognocchi",
            //      "url": "https://oiw.no/speaker/fabio-tognocchi-",
            //      "description": "Fabio Tognocchi is a Strategic Development Expert with extensive experience in sustainability and environmental management. He oversees strategic and innovative projects within the Erion System and its Producers. His responsibilities include managing packaging recycling, conducting studies and research to enhance recycling streams.",
            //      "@type": "EducationGroup",
            //      "@id": "dmhYqF2iO5Llcr6O4RZHxa",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/23ef8147f3a6eff7786cbf0e49f00a8d1d47637c-1250x1250.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Marco Vesipa",
            //      "url": "https://oiw.no/speaker/marco-vesipa-",
            //      "description": "Marco Vesipa has held the position of Program Manager at MagnoLab Innovation District. In this role he enthusiastically coordinate research and innovation projects, presenting the center to potential customers and investors. His commitment is aimed at ensuring a solid and profitable start for MagnoLab, contributing to its success.",
            //      "@type": "EducationGroup",
            //      "@id": "dmhYqF2iO5Llcr6O4RaHNr",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/40dfa43b81fcd89fcdebb77227eb409b1b56af98-1250x1250.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Niccolò Cipriani",
            //      "url": "https://oiw.no/speaker/niccolò-cipriani",
            //      "description": "Niccolò Cipriani Rifo with a crowdfrunding campaign, now it is a company with around 3M€ in turnover and more than 20 employees. Rifò is one of the leading brands in the sustainable fashion industry in Europe, our sales come from 10 different European countries and are our business model is multichannel. ",
            //      "@type": "EducationGroup",
            //      "@id": "LxW2c7ek5iDOrc2j9IxB5M",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/32ed6f00c26e19fa1edfdcf32b2e9d0ceb165ed0-1250x1250.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Gisle Mariani Mardal",
            //      "url": "https://oiw.no/speaker/gisle-mariani-mardal",
            //      "description": "Gisle Mariani Mardal is head of innovation and development. He has had a central role in the development of the Norwegian fashion industry since the establishment of the Norwegian Fashion Institute in 2009. His area of ​​expertise is strategic development and financing, with a background in design, innovation and entrepreneurship.",
            //      "@type": "EducationGroup",
            //      "@id": "LxW2c7ek5iDOrc2j9IxaV5",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/773f3c14b051ba51ded20b5a7965c1d2b13cfa51-1250x1250.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Pål Erik Haraldsen",
            //      "url": "https://oiw.no/speaker/pål-erik-haraldsen",
            //      "description": "Pål Erik has extensive experience in sales and entrepreneurship and has been involved in the textile industry for over 20 years. In recent years, he has worked actively to get more circular textiles in the health sector. Through this work, HWReuse! was established, which was later transformed into Norsk Tekstilgjenvinning AS.",
            //      "@type": "EducationGroup",
            //      "@id": "Vm1Iy7AzZo4BZhL7Fkzzrv",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ddd67d9d9795c720434a61d8dfcf4772370f7868-400x400.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Inge Schlapp-Hackl",
            //      "url": "https://oiw.no/speaker/inge-schlapp-hackl",
            //      "description": "Inge Schlapp-Hackl is working as a postdoc researcher at the department of Bioproducts and Biosystems at Aalto University, Finland. I graduated in 2018 at the Institute of General, Inorganic and Theoretical Chemistry at the University of Innsbruck in Austria. ",
            //      "@type": "EducationGroup",
            //      "@id": "toREhxqQ42TEspzFxZ3mle",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/02f2256d4b2202f2cd4b9feeca94d3b9ed8f844f-1600x2400.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "nfta24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "ClimateTech",
            //      "Seminar"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] From Material Girl to Material World: Pioneering Textiles Sending event 'From Material Girl to Material World: Pioneering Textiles' to importer
            //{
            //  "name": "BARN - Your Springboard into the US market",
            //  "url": "https://oiw.no/event/barn24",
            //  "startDate": "2024-09-25T08:00:00.000Z",
            //  "localStartDate": "2024-09-25T10:00",
            //  "endDate": "2024-09-25T09:30:00.000Z",
            //  "location": {
            //    "name": "Håndverkeren",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9148054,
            //      "longitude": 10.7396293
            //    },
            //    "@type": "Place",
            //    "@id": "lGrVwJKsOnpObjJ4QOaJWX",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "",
            //      "url": "https://www.norwayhouse.org/barn",
            //      "@id": "hQLKYFGzcQ7wYQCuKmpglF",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/54822a717075c41b3956c003d7cf0f40a7b165f7-600x200.png",
            //      "sameAs": [
            //        "https://www.norwayhouse.org/barn"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.norwayhouse.org/barn"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "",
            //      "url": "https://www.norwayhouse.org/",
            //      "@id": "69rK5Hd9X3fjRReZJwTE64",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/4b73514d025f8e9f3be9879fbb382416e300bd60-298x170.png",
            //      "sameAs": [
            //        "https://www.norwayhouse.org/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.norwayhouse.org/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Britt Ardakani",
            //      "url": "https://oiw.no/speaker/britt-ardakani",
            //      "description": "Britt has a Bachelor\u0027s degree in Computer Science and Mathematics from the University of Minnesota Duluth. Britt served as Vice Consul at the Honorary consulate in Minnesota, and served on the Minnesota Consular Corps, as Secretary/Treasurer, Vice president, and President. and a Board Member of the Norwegian American Chamber of Commerce since 2014.",
            //      "@type": "EducationGroup",
            //      "@id": "NO7Mjjgfso2MMqbm3uYl8r",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/82f4f7b77a960230356ad0a40fbcd5bdab2d34dc-506x642.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Craig Lervick",
            //      "url": "https://oiw.no/speaker/craig-lervick",
            //      "description": "Craig J. Lervick is an intellectual property attorney. He has extensive experience representing clients in patent prosecution and advising on patent issues. Craig also provides assistance in product design and development, helping clients to minimize infringement risks.",
            //      "@type": "EducationGroup",
            //      "@id": "NO7Mjjgfso2MMqbm3uYt1c",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/e550d26a25d9ed142536f191071e2e07cca21929-645x505.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Espen Jansen",
            //      "url": "https://oiw.no/speaker/espen-jansen",
            //      "description": "ESPEN is a CPA (certified public accountant, licensed in Colorado). He holds an MBA in financial management and a bachelor’s degree in business administration from the University of New Mexico. He also attended Bentley University’s McCallum Graduate School of Business. ",
            //      "@type": "EducationGroup",
            //      "@id": "NO7Mjjgfso2MMqbm3uZEKa",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ae15535e386a5c6939999e355654af8b49d1734f-810x1080.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Christina Carleton",
            //      "url": "https://oiw.no/speaker/christina-carleton",
            //      "description": "As the Chief Executive Officer of Norway House, a non-profit organization that promotes and celebrates the cultural, educational, and economic ties between Norway and the upper Midwest region, she has been leading the organization\u0027s strategic direction, operations, and partnerships since 2017. ",
            //      "@type": "EducationGroup",
            //      "@id": "sjpLtqPJGYKEfVADB7r9UC",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/3b75abcfdee3d3e264eb598392cac7f052f0ddb3-200x200.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Anya Brunson",
            //      "url": "https://oiw.no/speaker/anya-brunson",
            //      "description": "Anya Brunson leads the political and economic section at the U.S. Embassy in Oslo. Previously, she served as the Deputy Director for Middle East and Asia Energy Diplomacy office at the U.S. Department of State. She was a member of the Secretary of State’s team ensuring successful visits globally. ",
            //      "@type": "EducationGroup",
            //      "@id": "zd4Q4p4eupRPI1qc26c3MV",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/dc600da763f33c5c36bdcd2c65fc20f62fdf9ec5-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Amanda Taylor",
            //      "url": "https://oiw.no/speaker/amanda-taylor",
            //      "description": "Amanda Taylor is Vice President of Business Investment at the GREATER MSP\nPartnership. Amanda leads the team driving business attraction, expansion, and retention\nin the Minneapolis Saint Paul region, positioning the region for economic growth in the\nnext economy. She has 18 years of experience in economic development and\ncorporate site selection.",
            //      "@type": "EducationGroup",
            //      "@id": "Q38qk3JW92Upy4cwM96Wim",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/421665336b9253c2ee11300bd882d8a3e33f754b-506x506.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Jan Villard",
            //      "url": "https://oiw.no/speaker/jan-villard1",
            //      "description": "Jan Villard has been a Norwegian entrepreneur and business developer for three decades. With extensive experience in starting and developing numerous businesses in Norway, he is a co-owner of the brand agency Minnesota Agency AS, which is the initiator and largest owner of StartUSA Inc. From April 2024, he will be working from the USA.",
            //      "@type": "EducationGroup",
            //      "@id": "Q38qk3JW92Upy4cwMBlW0U",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/89df3d61e9046bef0becf14e57cad277654ac0e3-659x599.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "barn24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Scaling",
            //      "Panel"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] BARN - Your Springboard into the US market Sending event 'BARN - Your Springboard into the US market' to importer
            //{
            //  "name": "X KG a day: Unlocking the potential of reusable packaging",
            //  "url": "https://oiw.no/event/tomra24",
            //  "startDate": "2024-09-24T14:30:00.000Z",
            //  "localStartDate": "2024-09-24T16:30",
            //  "endDate": "2024-09-24T17:30:00.000Z",
            //  "location": {
            //    "name": "Æra Strategic Innovation",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9140746,
            //      "longitude": 10.7477461
            //    },
            //    "@type": "Place",
            //    "@id": "nNlMDwdaFpeRBQSKRWMVZs",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "TOMRA",
            //      "url": "https://www.tomra.com/en/",
            //      "@id": "3bSeV1kXTHmPOpL5818GYI",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/21409a7712e5b9319f52406562f742ba0435f6f5-1224x216.png",
            //      "sameAs": [
            //        "https://www.tomra.com/en/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.tomra.com/en/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Æra Strategic Innovation",
            //      "url": "https://www.era.as/",
            //      "@id": "nNlMDwdaFpeRBQSKRWPIEw",
            //      "sameAs": [
            //        "https://www.era.as/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.era.as/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Geir Sæther",
            //      "url": "https://oiw.no/speaker/geir-sæther",
            //      "description": "Geir Sæther holds a M.S. degree in Electronics from the Norwegian University of Science and Technology and a Master of Management degree from BI Norwegian School of Management. Since joining TOMRA in 1995, he has worked primarily with technology development but also within sales and marketing. He has led TOMRA Reuse since its inception in 2022. ",
            //      "@type": "EducationGroup",
            //      "@id": "3bSeV1kXTHmPOpL581C4pi",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/fbfd4c2e4ed92fdf50206c22b219bf5235013d13-640x640.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Bård Bringsrud Svensen",
            //      "url": "https://oiw.no/speaker/bård-bringsrud-svensen",
            //      "description": "Bård is the COO \u0026 Co-Founder of På(fyll), a company addressing plastic waste with a circular service as an alternative to single-use packaging. Previously he led sustainability projects Orkla Home \u0026 Personal Care and was a Product Developer at Orkla ASA. Passionate about sustainability, Bård seeks innovative solutions for everyday challenges.",
            //      "@type": "EducationGroup",
            //      "@id": "nNlMDwdaFpeRBQSKRWRwWg",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/fab09ccaf9b820452180b70f55f513d85d9344fc-640x640.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Fátima Sani",
            //      "url": "https://oiw.no/speaker/fátima-sani",
            //      "description": "This panel will be moderated by Fátima Sani. At TOMRA, Fátima works toward seeking solutions and partnerships to enable TOMRA’s reverse vending business in new markets. From her early career at Google, Fátima has pivoted from SaaS to sustainability. She resides in Oslo and is passionate about mitigating climate change and increasing circularity. ",
            //      "@type": "EducationGroup",
            //      "@id": "VdKfDJLLsUwX1ZJhWuLGaY",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/afeab39b30a29d66e8b5b6c68b1d2a68f4208288-640x640.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Anne Marheim Støren",
            //      "url": "https://oiw.no/speaker/anne-marheim-støren",
            //      "description": "Anne works with strategy and sustainability in Orkla Home and Personal Care (OHPC). She is a member of the Orkla Home and Personal Care Board of Directors, and holds a double MSc degree in Management from London School of Economics (LSE) and Marketing from University of Bath.",
            //      "@type": "EducationGroup",
            //      "@id": "VdKfDJLLsUwX1ZJhWuLlC4",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/48d286264b640271d12f1c4e1533d70bb886aaa3-640x640.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Ingrid Holtan Borka",
            //      "url": "https://oiw.no/speaker/ingrid-holtan-søbstad",
            //      "description": "Ingrid is a marine biologist whose work focuses on reducing plastic and marine littering. She has previously worked at the Norwegian Environment Agency focusing on climate change in the ocean and as Project Manager for Keep Norway Beautiful’s marine litter conference.",
            //      "@type": "EducationGroup",
            //      "@id": "DFEKOVjeVqrEbThYrimGg4",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/e0d23d451ddb7c031ac0f66cd8ea2ec06fd86638-640x640.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Simon Smedegaard Rossau",
            //      "url": "https://oiw.no/speaker/simon-smedegaard-rossau",
            //      "description": "Simon’s professional background is rooted in environmental planning and green tech, and he has worked with shaping and managing large-scale waste sorting systems at mega-events in Denmark. Simon now leads the takeaway and to-go packaging project for Aarhus Municipality now being piloted together with TOMRA.",
            //      "@type": "EducationGroup",
            //      "@id": "SDLUuTWhTFC5UwnLL4H9wS",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ba7fea8b8e287b93f3a38687a25ebe9a06285a40-640x640.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "tomra24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Scaling",
            //      "Impact",
            //      "Panel"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] X KG a day: Unlocking the potential of reusable packaging Sending event 'X KG a day: Unlocking the potential of reusable packaging' to importer
            //{
            //  "name": "The Dilution Dilemma",
            //  "url": "https://oiw.no/event/nordea-bank24",
            //  "startDate": "2024-09-24T13:00:00.000Z",
            //  "localStartDate": "2024-09-24T15:00",
            //  "endDate": "2024-09-24T15:00:00.000Z",
            //  "location": {
            //    "name": "Nordea Bank Hovedkontor, Essendrops gate 7, Oslo",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9292493,
            //      "longitude": 10.7082925
            //    },
            //    "@type": "Place",
            //    "@id": "AfyuFOmFmEYg7iijoH9EQu",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Nordea",
            //      "url": "https://www.nordea.no/",
            //      "@id": "AfyuFOmFmEYg7iijoH9HXF",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/4ac5a99beea5097a8419f7ef4ae6902bd6258014-600x264.png",
            //      "sameAs": [
            //        "https://www.nordea.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.nordea.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Innovayt AS",
            //      "url": "https://innovayt.eu/",
            //      "@id": "J2DRDH9K5wKx8x8fzuCYco",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/1d1b8e03f13fcf5e35f5d8179a899ec8d194ffe0-5000x1069.png",
            //      "sameAs": [
            //        "https://innovayt.eu/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://innovayt.eu/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Marianne Bratt Ricketts",
            //      "url": "https://oiw.no/speaker/marianne-bratt-ricketts",
            //      "description": "Marianne er norsk seriegründer med tidligere virksomheter innen teknologi, design, kom., og vintersport. \n\nI 2016 grunnla hun videoteknologi-startupen VIBBIO. Hun bygget selskapet gjennom 6 år og i 2022 ble det kjøpt av det børsnoterte selskapet ON24 Inc. \n\nI 2024 er Marianne tilbake på startupscenen i Oslo, med ny satsning som heter Scalio. ",
            //      "@type": "EducationGroup",
            //      "@id": "LxW2c7ek5iDOrc2j9LI7AN",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/93296c10176309a42651e164612b1b05ebbd3583-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Igor Pancevski",
            //      "url": "https://oiw.no/speaker/igor-pancevski",
            //      "description": "Med Makedonsk opprinnelse, kom til Norge på slutten av 90 tallet. I 1997 fikk jeg min første IT jobb og siden 2002 har jeg startet flere små IT selskaper. I 2014 startet jeg Neisa Norway AS som etter hvert vokste til 97 ansatte og med kunder som HP, Lenovo, IBM, Xerox, solgte jeg den i 2020. \nSiden da, har jeg investert i eiendom og i Sparkpark AS\n",
            //      "@type": "EducationGroup",
            //      "@id": "v3431n7IAaIx7DOZmSbWQt",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/926b2fdac0995ee4692b828685ef6f536c67d89d-785x963.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Torbjørn Furuseth",
            //      "url": "https://oiw.no/speaker/torbjørn-furuseth",
            //      "description": "Torbjørn Furuseth, a Medical Doctor and experienced executive, has led R\u0026D-driven ventures for over 10 years, including as CFO in 2 startups/scaleups, where he raised \u003e30M€.\nCurrently he is the Co-founder and CEO of DoMore Diagnostics, that is pioneering AI in cancer diagnostics and recently secured up to €10M in funding through the EIC Accelerator",
            //      "@type": "EducationGroup",
            //      "@id": "29cfuvScEVHxUPeK7hrkIC",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/9a73589fb7aed90ad1b3a9cb8f06d457c37bcb8b-750x999.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Åsmund Lunnan Bjørnstad",
            //      "url": "https://oiw.no/speaker/åsmund-lunnan-bjørnstad",
            //      "description": "Åsmund Lunnan Bjørnstad is a Senior Business Developer and Venture Investor at Kongsberg Innovation, with over 10 years of experience in sales, business development, and investment. Previously at PwC, DeepTech Alliance, and Capgemini Invent, he now focuses on assessing and investing in innovative deep-tech companies at Kongsberg Innovation.",
            //      "@type": "EducationGroup",
            //      "@id": "29cfuvScEVHxUPeK7iILFc",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/57c5c80b7dc34520e667440e5af56652fd410130-1200x1500.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Cecilie Skjong",
            //      "url": "https://oiw.no/speaker/cecilie-skjong1",
            //      "description": "Cecilie has experience as Director of Corporate Development at the SaaS startup Aize, where she was part of the team from the early beginning. She also has 5 years experience as a management consultant at McKinsey \u0026 Co. Today she works as an Investment Manager at Skyfall.",
            //      "@type": "EducationGroup",
            //      "@id": "VBlNaGbxeXMiFaeMV8jhdS",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/0ac95ad570b405f1119e1160994533b1b57b051c-183x275.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Solveig Ellila Kristiansen",
            //      "url": "https://oiw.no/speaker/solveig-ellila-kristiansen",
            //      "description": "Solveig Ellila Kristiansen is an experienced executive with a strong background in leading international technology firms, particularly in scaling PE-backed companies to profitable exits. She currently is the CEO and investor at Granfoss, a marine services startup specializing in pioneering electric underwater robotics for emission-free operations.",
            //      "@type": "EducationGroup",
            //      "@id": "EPBqwUaDv2CRt5j0fkx23E",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/5a3150854113ff69aeb5d175c27f3bfde7233bdd-1000x1250.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "nordea-bank24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Scaling",
            //      "Talk"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] The Dilution Dilemma Sending event 'The Dilution Dilemma' to importer
            //{
            //  "name": "Founders Live Pitch Contest - Across 160 Cities Globally",
            //  "url": "https://oiw.no/event/founders-live24",
            //  "startDate": "2024-09-24T15:00:00.000Z",
            //  "localStartDate": "2024-09-24T17:00",
            //  "endDate": "2024-09-24T18:00:00.000Z",
            //  "description": "\n🚀 Exciting news! Founders Live, the renowned startup pitching community, has arrived in the Nordics and we\u0027re thrilled to host it for the second time in Oslo! Join us for an electrifying event where top-tier entrepreneurs and investors come together to invest, inspire, educate, and entertain.\n\n👩‍💼 Calling all founders! Dive into a vibrant network of local and global founders and investors within the Founders Live community. Let\u0027s connect and take your startup to new heights!\n\n💼 Investors, this is your chance! Connect with a diverse array of world-class startups and fellow investors. Don\u0027t miss out on this opportunity to explore groundbreaking investment possibilities.\n\n🌍 Experience the thrill of our Livestreamed event, featuring exciting 99-second pitch competitions from over 85 cities worldwide. Get ready to be inspired and ignite your entrepreneurial spirit! Don\u0027t miss this unforgettable event! #FoundersLive #StartupCommunity #InvestmentOpportunity",
            //  "location": {
            //    "name": "Mesh Nationaltheatret",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9130126,
            //      "longitude": 10.7341709
            //    },
            //    "@type": "Place",
            //    "@id": "FyAHGzCGYvVljtOszk0rrs",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Desinova Studios",
            //      "url": "https://www.desinovastudios.com/",
            //      "@id": "FyAHGzCGYvVljtOszk3DTA",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/d05e78028ecd1ab38f5a408799f1cacfa568abf8-742x308.png",
            //      "sameAs": [
            //        "https://www.desinovastudios.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.desinovastudios.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Mesh Community",
            //      "url": "https://meshcommunity.com/",
            //      "@id": "75hF6u0k3iHYTKl66ONYs6",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/c7ae50dc4a4788ef07d1f78d30bbdcafc696d80e-2826x830.png",
            //      "sameAs": [
            //        "https://meshcommunity.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://meshcommunity.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Euro Nordic Funding Alliance",
            //      "url": "https://www.en-fa.org/",
            //      "@id": "sjpLtqPJGYKEfVADB8veba",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/048588232fe7576d4d93f97818f358e30417ac55-2522x986.png",
            //      "sameAs": [
            //        "https://www.en-fa.org/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.en-fa.org/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "founders-live24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Networking",
            //      "Business Conference"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Founders Live Pitch Contest - Across 160 Cities Globally Sending event 'Founders Live Pitch Contest - Across 160 Cities Globally' to importer
            //{
            //  "name": "The Pluriversal Playhouse: Imagining a Multitude of Futures ",
            //  "url": "https://oiw.no/event/pluriversal-playground24",
            //  "startDate": "2024-09-24T09:00:00.000Z",
            //  "localStartDate": "2024-09-24T11:00",
            //  "endDate": "2024-09-24T12:00:00.000Z",
            //  "description": "Welcome to The Pluriversal Playhouse.\n\nWe invite you to imagine and inquire about pluriverse: the idea of a multitude of worlds and futures. This cinematic journey is a creative exploration and melting pot of ideas, concepts, knowledge and stories. It’s a combination of In Conversation, film screening, and talks. \n\nGet your invitation here: https://bit.ly/3A6iHN9 \n\nIn-person \u0026 Online\n",
            //  "location": {
            //    "name": "Deichman Bjørvika (Kinosalen)",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": " Oslo Public Library",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9087508,
            //      "longitude": 10.7500277
            //    },
            //    "@type": "Place",
            //    "@id": "q2pseckBWkABOLusJhvDSb",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "The Big Picture",
            //      "url": "https://thebigpicture.co/",
            //      "@id": "jY3ou9bQYCSswQTCA3whl0",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/77892798f83afff920362dcfb8962b61015ebddc-1201x1201.png",
            //      "sameAs": [
            //        "https://thebigpicture.co/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://thebigpicture.co/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Sahana Chattopadhyay",
            //      "url": "https://oiw.no/speaker/sahana-chattopadhyay",
            //      "description": "Sahana Chattopadhyay works at the intersection of human potential, narratives and meaning-making, systems sensing, technology, and sensemaking.\n\nShe writes about civilizational transition, decolonial futures, emergent learning, leading in liminal times, and new narratives.\n\nShe is the Founder of a boutique consulting firm in Mumbai, Proteeti.",
            //      "@type": "EducationGroup",
            //      "@id": "LxW2c7ek5iDOrc2j9ObOsC",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/941aa89813c6b6394fbcbf45afdd65ab198056a7-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Dr. Wilfred Ukpong",
            //      "url": "https://oiw.no/speaker/dr-wilfred-ukpong",
            //      "description": "Dr. Wilfred Ukpong\n\nDr. Wilfred Ukpong is a French-Nigerian interdisciplinary artist, scholar and practice-based researcher whose distinctive socially engaged practice utilizes several interwoven mediums to tackle pertinent social issues. Dr Ukpong will talk about the perspective of the pluriverse from an Afrofuturism perspective.\n",
            //      "@type": "EducationGroup",
            //      "@id": "v3431n7IAaIx7DOZmVa3DW",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/5c1b4a7412a91a474f106b307a2cb4414c432ca7-1200x1234.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Emmanuela Shinta",
            //      "url": "https://oiw.no/speaker/emmanuela-shinta",
            //      "description": "Emmanuela Shinta is a Dayak changemaker, leader, activist, environmentalist, filmmaker and writer with a reputation for leading and empowering young Indigenous Dayak people in Kalimantan, Indonesia. She is the founder of Ranu Welum. She will talk about what the future means from an Indigenous community perspective amidst ecological disaster.",
            //      "@type": "EducationGroup",
            //      "@id": "jjBq4X6gdc6IuElj7oHbku",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/7292678600e2aaeebcf5774bf62c553355119412-1332x1372.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Kite a.k.a Dr. Suzanne Kite",
            //      "url": "https://oiw.no/speaker/kite-a-k-a-dr-suzanne-kite",
            //      "description": "Kite  is an Oglála Lakȟóta performance artist, visual artist, and composer. She is Director of Wihanble S’a Lab, Distinguished Artist in Residence and Assistant Professor of American and Indigenous Studies, Bard College. She is a Research Associate and Residency Coordinator for the Abundant Intelligences (Indigenous AI) project.\n",
            //      "@type": "EducationGroup",
            //      "@id": "jjBq4X6gdc6IuElj7oHeMT",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/31fcefb80f7e51ca7c4898ecff3f5ed872e570c0-1366x2049.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "pluriversal-playground24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Networking",
            //      "Fireside"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] The Pluriversal Playhouse: Imagining a Multitude of Futures  Sending event 'The Pluriversal Playhouse: Imagining a Multitude of Futures ' to importer
            //{
            //  "name": "Speedfriending ",
            //  "url": "https://oiw.no/event/speedfriending24",
            //  "startDate": "2024-09-23T10:00:00.000Z",
            //  "localStartDate": "2024-09-23T12:00",
            //  "endDate": "2024-09-23T11:50:00.000Z",
            //  "description": "",
            //  "location": {
            //    "name": "Mesh Nationaltheatret",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9326743,
            //      "longitude": 10.6675753
            //    },
            //    "@type": "Place",
            //    "@id": "gtrSyynP7P1x0DLKmoAEb0",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Speedfriending",
            //      "url": "https://www.speedfriending.com/",
            //      "@id": "FNKpwkKRimvSChfWMsZsN4",
            //      "sameAs": [
            //        "https://www.speedfriending.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.speedfriending.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Viktor Sanden",
            //      "url": "https://oiw.no/speaker/viktor-sanden",
            //      "@type": "EducationGroup",
            //      "@id": "bJU0rnud4tYDJnYMKHaNgJ",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "speedfriending24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Networking",
            //      "Impact"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Speedfriending  Sending event 'Speedfriending ' to importer
            //{
            //  "name": "Breaking Barriers: Scaling and International Expansion",
            //  "url": "https://oiw.no/event/tech-nordic-advocates24",
            //  "startDate": "2024-09-26T12:00:00.000Z",
            //  "localStartDate": "2024-09-26T14:00",
            //  "endDate": "2024-09-26T15:00:00.000Z",
            //  "description": "https://www.instagram.com/technordicadvocates/\nhttps://www.linkedin.com/company/tech-nordic-advocates/",
            //  "location": {
            //    "name": "Epicenter Oslo",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": " 0166 Oslo",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9189846,
            //      "longitude": 10.7329332
            //    },
            //    "@type": "Place",
            //    "@id": "S8WVnhHTDNMkkNUC8Z3SwN",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Tech Nordic Advocates",
            //      "url": "https://www.technordicadvocates.org/",
            //      "@id": "kxwpvIU8xK3rqdBEdF3Hpw",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/9373f5d030734e34ed55499cd1f276c6992d597f-204x192.png",
            //      "sameAs": [
            //        "https://www.technordicadvocates.org/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.technordicadvocates.org/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "tech-nordic-advocates24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Scaling",
            //      "Networking",
            //      "Seminar"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Breaking Barriers: Scaling and International Expansion Sending event 'Breaking Barriers: Scaling and International Expansion' to importer
            //{
            //  "name": "DaringHER: The Science of Success for Female Founders",
            //  "url": "https://oiw.no/event/do-business-better-school24",
            //  "startDate": "2024-09-25T11:00:00.000Z",
            //  "localStartDate": "2024-09-25T13:00",
            //  "endDate": "2024-09-25T13:30:00.000Z",
            //  "description": "DaringHER is an educational event for female founders. This year\u0027s DaringHER theme is:\nThe Science of Success - Using Neuroscience and Mindset Tools to Accomplish Big Things with Balance.\n\nIf you are a female founder and want to learn the latest neuroscience-based mindset tools and strategies to boost business growth WHILE lowering stress, this is the room for you at Oslo Innovation Week 2024.  We\u0027ll see you there!",
            //  "location": {
            //    "name": "Youngs Oslo",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9142883,
            //      "longitude": 10.749196
            //    },
            //    "@type": "Place",
            //    "@id": "nNlMDwdaFpeRBQSKRVwPsc",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "The Do Business Better School",
            //      "url": "https://dobusinessbetterschool.com/",
            //      "@id": "j0P7aBo0bkM6HAdsmhtg8e",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/06d15ccf979f28a7fbfd1fa63b5684111f408a7c-1000x400.png",
            //      "sameAs": [
            //        "https://dobusinessbetterschool.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://dobusinessbetterschool.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "GroGro",
            //      "url": "https://grogro.no/",
            //      "@id": "j0P7aBo0bkM6HAdsmhtoqQ",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/76c33e4ffb09de16644c350d3bfe38388a7b004d-2556x2330.png",
            //      "sameAs": [
            //        "https://grogro.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://grogro.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Kari Elizabeth Enge",
            //      "url": "https://oiw.no/speaker/kari-elizabeth-enge",
            //      "description": "Kari Elizabeth Enge is an American social entrepreneur turned business coach who helps purpose-driven founders to increase income and impact without sacrificing their personal lives or wellbeing. Kari has traveled to over 40 countries and blends colorful storytelling and innovative neuroscience, with practical strategies when teaching founders.",
            //      "@type": "EducationGroup",
            //      "@id": "3bSeV1kXTHmPOpL580JzY6",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/5ae97ffedb829ed9f76297e1e301ea231ec5c5fe-503x630.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Malin Bruset",
            //      "url": "https://oiw.no/speaker/malin-bruset",
            //      "description": "Malin Bruset is a Swedish entrepreneur, writer, Doctor of Naprapathy and the founder of GroGro, the first fresh-ready food for kids. Malin is passionate not only about revolutionizing the food industry, she is also passionate about balancing life as a mom, wife and dog owner while building a thriving business. ",
            //      "@type": "EducationGroup",
            //      "@id": "gtrSyynP7P1x0DLKmqXfM0",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/24fead1069e93da52381003688cf2d542718eaab-2419x2818.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "do-business-better-school24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Impact",
            //      "Scaling",
            //      "Workshop"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] DaringHER: The Science of Success for Female Founders Sending event 'DaringHER: The Science of Success for Female Founders' to importer
            //{
            //  "name": "Official Opening",
            //  "url": "https://oiw.no/event/official-opening",
            //  "startDate": "2024-09-23T14:00:00.000Z",
            //  "localStartDate": "2024-09-23T16:00",
            //  "endDate": "2024-09-23T17:00:00.000Z",
            //  "description": "Join us at Høymagasinet for Oslo Innovation Week 2024\u0027s official opening. Celebrate pioneers with the Oslo Innovation Award, a panel on scaling, and networking with founders, entrepreneurs, investors, and corporates. Let\u0027s kick off the week in style!\"",
            //  "location": {
            //    "name": "Høymagasinet",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9094773,
            //      "longitude": 10.7179184
            //    },
            //    "@type": "Place",
            //    "@id": "968ccbcf-0bf6-4214-8854-a7c746608fe0",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Oslo Kommune",
            //      "url": "https://www.oslo.kommune.no/#gref",
            //      "@id": "4ba7b6bf-a273-417b-b237-e7a69039af2e",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/3687e810d7a1b744b08a57e5de7077ec6155aa83-1372x934.png",
            //      "sameAs": [
            //        "https://www.oslo.kommune.no/#gref"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.oslo.kommune.no/#gref"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Innovation Norway",
            //      "url": "https://en.innovasjonnorge.no/",
            //      "@id": "bbcbf613-3359-4f55-abbe-9b200f29dbb4",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ba263ea26bba3fce42df9fd9e6c3fa54b235e567-4481x1953.png",
            //      "sameAs": [
            //        "https://en.innovasjonnorge.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://en.innovasjonnorge.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Oslo Innovation Week",
            //      "url": "https://oiw.no/",
            //      "@id": "2fea029b-1150-40c1-a191-ebbb24a6910d",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/92eee2b3af2cf96baf3b994ca8c0214b70b0a6aa-3167x2001.png",
            //      "sameAs": [
            //        "https://oiw.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Håkon Haugli",
            //      "url": "https://oiw.no/speaker/håkon-haugli",
            //      "description": "Håkon Haugli is the CEO of Innovation Norway, the government’s most important instrument for innovation and development of Norwegian enterprises and industry. Prior to joining Innovation Norway in 2019, he was the Managing Director of Abelia, the business association of Norwegian knowledge and technology-based enterprises, a part of the Norwegian Confederation of Enterprise (NHO). From 2009 to 2013 he was a Member of Parliament, representing the Labour Party and the Oslo district. His prior work experience includes Gjensidige (insurance), McKinsey \u0026 Co and ISCO Group (consulting). He has held a number of Board positions and has a law degree from the University of Oslo.",
            //      "@type": "EducationGroup",
            //      "@id": "d71cbb07-9c64-49e6-9fb3-a11d66e57c11",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/fda752410565303c9402a8510752839e6faf1911-2048x1152.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Ravi Belani",
            //      "url": "https://oiw.no/speaker/ravi-belani1",
            //      "description": "Ravi Belani is the Founder and Managing Director of \nAlchemist Accelerator\n, a venture backed accelerator for enterprise startups. Ravi is also an Adjunct Lecturer at Stanford University, where he leads the Entrepreneurial Thought Leaders Seminar (the largest class on entrepreneurship at Stanford). He is an early Investor in Twitch / Justin.TV, Pubmatic, Rigetti, LaunchDarkly.",
            //      "@type": "EducationGroup",
            //      "@id": "1a767960-a83b-4754-8715-e8e9375df1f8",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/85aaf63b4ae84e295e0aaa274095e0bd5f1a506b-900x506.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Lauga Oskarsdottir",
            //      "url": "https://oiw.no/speaker/lauga-oskarsdottir",
            //      "description": "Lauga has international experience in business development from the shipping industry in New York, and has worked in the IT industry since 2013 as a consultant and entrepreneur. Before joining Noora, she managed the incubator in StartupLab. She holds a BBA from Berkeley College 2012. ",
            //      "@type": "EducationGroup",
            //      "@id": "e0086016-f4ee-40cb-ac80-ae76f74414ef",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/62d8f8a561f0a0227e05aefbfd96c13db81ff5e0-960x548.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "official-opening",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Networking",
            //      "Community",
            //      "Launch"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Official Opening Sending event 'Official Opening' to importer
            //{
            //  "name": "lululemon x Mesh – Morning Run",
            //  "url": "https://oiw.no/event/lululemon-x-mesh-morning-run",
            //  "startDate": "2024-09-23T06:30:00.000Z",
            //  "localStartDate": "2024-09-23T06:30:00.000Z",
            //  "endDate": "2024-09-23T07:30:00.000Z",
            //  "location": {
            //    "name": "Lululemon (Karl Johans gate 23)",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9128912,
            //      "longitude": 10.7400882
            //    },
            //    "@type": "Place",
            //    "@id": "2bd6a3ca-d542-4e4c-967d-47b83a72dbe8",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Mesh Community",
            //      "url": "https://meshcommunity.com/",
            //      "@id": "HJ5uo4LMcm2DGW55qrGO6E",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/d48bebc08c7a61f88361875a5e7c738f02ff8126-5178x1601.png",
            //      "sameAs": [
            //        "https://meshcommunity.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://meshcommunity.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "lululemon-x-mesh-morning-run",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Community",
            //      "Sport"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] lululemon x Mesh – Morning Run Sending event 'lululemon x Mesh – Morning Run' to importer
            //{
            //  "name": "Data \u0026 AI in Life Sciences: International Collaboration",
            //  "url": "https://oiw.no/event/lpo24",
            //  "startDate": "2024-09-26T14:00:00.000Z",
            //  "localStartDate": "2024-09-26T16:00",
            //  "endDate": "2024-09-26T17:30:00.000Z",
            //  "location": {
            //    "name": "Oslo Science Park",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9421409,
            //      "longitude": 10.7139813
            //    },
            //    "@type": "Place",
            //    "@id": "5ObSRLPRVl3eQGU5fd07HJ",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "LPO (Lithuanian Professionals\u0027 Club in Oslo)",
            //      "url": "https://www.lncc.no/lpo/lpo-about/",
            //      "@id": "3bSeV1kXTHmPOpL581PY4M",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/8fc30f96a9da217c680eeb5c5b92b6f20ecad08e-4207x4207.png",
            //      "sameAs": [
            //        "https://www.lncc.no/lpo/lpo-about/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.lncc.no/lpo/lpo-about/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Embassy of Lithuania in Norway ",
            //      "url": "https://no.mfa.lt",
            //      "@id": "azBm0ox0gTIvJxtHPtyAxc",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/093be33256f869f60526ebd3e0848276981bcabc-2022x1484.png",
            //      "sameAs": [
            //        "https://no.mfa.lt"
            //      ],
            //      "gogo": {
            //        "webpage": "https://no.mfa.lt"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Innovation Agency Lithuania",
            //      "@id": "d6WG2Qf7Ibc1mUxMpJBQaB",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/4bec054745000f2cfc894b2a717392846b2b5281-2187x1061.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Royal Norwegian Embassy in Vilnius",
            //      "@id": "VBlNaGbxeXMiFaeMV9GdyE",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/4ab66dae075090dade58c5e39f3f395c662a9904-1000x481.png",
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Dominykas Milašius",
            //      "url": "https://oiw.no/speaker/dominykas-milašius",
            //      "description": "Dom is a deep tech entrepreneur and investor with a decade in risk advisory across EMEA. At Baltic Sandbox Ventures, he scouts DeepTech founders and scales companies. He co-founded Delta Biosciences to accelerate drug discovery. Split between London and Vilnius, he’s a Paris Institute of Political Studies alumnus and a LitBAN member.",
            //      "@type": "EducationGroup",
            //      "@id": "VBlNaGbxeXMiFaeMV8wOCQ",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/b930a3504aa5f3970c34810796716007e628ccf5-640x960.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Antanas Montvila",
            //      "url": "https://oiw.no/speaker/antanas-montvila",
            //      "description": "Antanas Montvila, MD, MSc, is a radiologist and Chief Innovation Officer at the Hospital of Lithuanian University of Health Sciences Kaunas Clinics, the largest hospital in the Baltics. He leads projects in telemedicine and data standardization. ",
            //      "@type": "EducationGroup",
            //      "@id": "29cfuvScEVHxUPeK7iN3bU",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/1a261e8fd0a4bc22f541a42a2589ec5ee3680516-400x400.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Daniel Naumovas",
            //      "url": "https://oiw.no/speaker/daniel-naumovas",
            //      "description": "For the past 5 years, Daniel has been the head of the biobank and a senior researcher at Vilnius University Hospital Santaros Klinikos, leading a team of 10 in biobanking, cryobiology, ELSI, advanced therapies, ex-vivo cancer studies, drug screening, biomarker research, and single-cell genomics. He has given over 150 interviews in 4 languages.",
            //      "@type": "EducationGroup",
            //      "@id": "VJwDF6OhsypgL07DCWZa05",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/c826def9641357020bd3140bf851daa9d8745b09-400x400.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Ole Alexander Opdalshei",
            //      "url": "https://oiw.no/speaker/ole-alexander-opdalshei",
            //      "description": "Ole Alexander Opdalshei is Deputy Secretary General and Director General for Research, Prevention, and Cancer Treatment at the Norwegian Cancer Society. With a social science background, he has extensive experience in public administration and research, and has represented the Society on various councils and boards.",
            //      "@type": "EducationGroup",
            //      "@id": "VJwDF6OhsypgL07DCWa6rl",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/74bb6ac62cb6cf9a00f59a86792923731fd664cc-400x400.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Nasim Bergman Farrokhnia",
            //      "url": "https://oiw.no/speaker/nasim-bergman-farrokhnia",
            //      "description": "Nasim Farrokhnia, MD, PhD, MBA, is a Healthcare Executive at Microsoft Western Europe, overseeing 12 countries and the Nordics. She has extensive experience in clinical research, life sciences, and leadership. ",
            //      "@type": "EducationGroup",
            //      "@id": "29cfuvScEVHxUPeK7iNJT6",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/012a1bbdc118ddcdbd3fec78e6995e9ba12d8075-640x640.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Karolina Trakšelytė-Rupšienė",
            //      "url": "https://oiw.no/speaker/karolina-trakšelytė-rupšienė",
            //      "description": "Karolina is a smart specialization coordinator at Innovation Agency Lithuania - Lithuania’s business competitiveness partner in a global world.  With PhD in chemical engineering and industrial background currently she drives innovation by fostering collaboration between academia and industry in life sciences.",
            //      "@type": "EducationGroup",
            //      "@id": "VJwDF6OhsypgL07DCWdTRa",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/fc86577a0db7857bb9240af17835508d92e99dc2-640x426.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Rita Sakus",
            //      "url": "https://oiw.no/speaker/rita-sakus",
            //      "description": "Business angel investor and board member of LitBAN and the European Business Angel Network. Born in Toronto with an MBA from Boston, she managed Israel’s investment portfolio at Corporate Ventures. Since moving to Lithuania, she has driven start-up innovation, taught venture capital at ISM, and chaired Vilnius University’s MBA thesis committee.",
            //      "@type": "EducationGroup",
            //      "@id": "VJwDF6OhsypgL07DCWgWlS",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/4cf3c5816ed6775cd97ef1d8b6a2a92acc44f5cd-640x640.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Laura Korsakova",
            //      "url": "https://oiw.no/speaker/laura-korsakova",
            //      "description": "Laura Korsakova is the Co-founder and CEO of Psylink, advancing nature-based molecule biosynthesis through synthetic biology. Previously, she conducted pharmacology research and worked in cell therapy, bringing advanced oncology products to market. Now, she focuses on developing psychedelic-inspired molecules for mental health disorders.",
            //      "@type": "EducationGroup",
            //      "@id": "VJwDF6OhsypgL07DCWgpw6",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/f5d262f78d5c0b7a9c7656f0f476d51678958621-400x400.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Dr. Klas Pettersen",
            //      "url": "https://oiw.no/speaker/dr-klas-pettersen",
            //      "description": "CEO of NORA.ai, a Norwegian consortium focused on AI, machine learning, and robotics. With a physics master’s from NTNU and a PhD in computational neuroscience from NMBU, he has applied AI to brain research as a postdoc and research scientist. His career includes roles at the University of Oslo, NMBU, UC San Diego, and as an informatics consultant.",
            //      "@type": "EducationGroup",
            //      "@id": "VJwDF6OhsypgL07DCWhDdz",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/d4a99a57cc90ca68c3aa3fb3e10076d3fdd741b9-640x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "lpo24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "HealthTech",
            //      "Networking"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Data & AI in Life Sciences: International Collaboration Sending event 'Data & AI in Life Sciences: International Collaboration' to importer
            //{
            //  "name": "From Ideas to Impact: Leading Corporate Innovation ",
            //  "url": "https://oiw.no/event/sprint-consulting24",
            //  "startDate": "2024-09-26T15:00:00.000Z",
            //  "localStartDate": "2024-09-26T17:00",
            //  "endDate": "2024-09-26T17:00:00.000Z",
            //  "description": "From Ideas to Impact: Leading Corporate Innovation \n\nJoin us at Oslo Innovation Week 2024 for a panel discussion on corporate innovation. Joining us on the stage are innovators from Norway\u0027s leading corporates.\n\nYou will learn about strategic approaches, best practices, and the future of corporate innovation. And the triumphs and setbacks that come with innovation processes. \n\nOur panelists: \nGustav Eilertsen, Vice President Innovation and Venturing, Fortum\nBing Zhao, Vice President New Business Development, TOMRA\nTrond Atle Smedsrud, CEO Emerging Business, Aker BioMarine\n\nThe panel will be moderated by Sigrid Rennemo\n\nDon\u0027t miss this opportunity to network with industry leaders and gain insights to drive your organization forward. Secure your spot now! \n",
            //  "location": {
            //    "name": "Karl Johans gate 14, 0154 Oslo",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": " 0154 Oslo",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9119889,
            //      "longitude": 10.7424432
            //    },
            //    "@type": "Place",
            //    "@id": "Ae7ZTd1CwzH5RnEGIvnBAb",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Sprint Consulting ",
            //      "url": "https://www.sprint.no/",
            //      "@id": "j0P7aBo0bkM6HAdsmiimW6",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/645516e96df5112fef8279c9badae59022d6733c-4328x2065.png",
            //      "sameAs": [
            //        "https://www.sprint.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.sprint.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Sigrid Rennemo",
            //      "url": "https://oiw.no/speaker/sigrid-rennemo",
            //      "description": "Sigrid Rennemo is a partner at Sprint and one of Norway\u0027s most renowned innovation advisors. She has over 10 years of experience in managing and organizing innovation processes across a variety of sectors. In addition to building Sprint’s innovation services, she has contributed to developing well-known concepts and companies on behalf of Sprint’s ",
            //      "@type": "EducationGroup",
            //      "@id": "29cfuvScEVHxUPeK7hwxR6",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/c1838eaf7f7dfe8b07d4b2e05470bbc18e5f24f4-1080x1080.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Gustav Eilertsen",
            //      "url": "https://oiw.no/speaker/gustav-eilertsen",
            //      "description": "Gustav has extensive experience as a manager and board member, with a proven track record in fast-paced, complex markets. He is skilled in sales, marketing, change management, innovation, and business development. Passionate about digital transformation and technology, Gustav is a strong business development professional with degrees from BI and MB",
            //      "@type": "EducationGroup",
            //      "@id": "VJwDF6OhsypgL07DCTXDxI",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/c09f0926e8f094b8e87150dbbe158b6595ca2754-622x621.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Bing Zhao",
            //      "url": "https://oiw.no/speaker/bing-zhao",
            //      "description": "Bing Zhao leads New Business Development at TOMRA, a global leader in the circular economy, driving the resource revolution. She has extensive leadership experience as the Regional Head of a high-growth business unit in Asia, and is passionate about implementing technologies and systems for meaningful, lasting impact.",
            //      "@type": "EducationGroup",
            //      "@id": "29cfuvScEVHxUPeK7hxXHo",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ae8fc0c30415e47561f67e2d21075d1b6abe8330-1982x2595.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Trond Atle Smedsrud",
            //      "url": "https://oiw.no/speaker/trond-atle-smedsrud",
            //      "description": "Trond Atle Smedsrud is the CEO of Emerging Business at Aker BioMarine, focusing on maximizing the value of companies like Aion, Epion/Kori, Understory, and Qpaws. He has a diverse background in innovation, business development, marketing, communication, and regulatory affairs, with leadership experience in both the U.S. and Norway.",
            //      "@type": "EducationGroup",
            //      "@id": "GBgGvFlp21zYJWMNekfWag",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/eead5a55e0c4e0d86872f47bc14a369f3a826ed2-299x299.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "sprint-consulting24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Community",
            //      "Impact",
            //      "Panel"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] From Ideas to Impact: Leading Corporate Innovation  Sending event 'From Ideas to Impact: Leading Corporate Innovation ' to importer
            //{
            //  "name": " Innovate \u0026 Play with a Community Trivia MESH \u0026 KOK | Mesh Community Stage",
            //  "url": "https://oiw.no/event/mesh-community-stage-kok",
            //  "startDate": "2024-09-26T15:00:00.000Z",
            //  "localStartDate": "2024-09-26T17:00",
            //  "endDate": "2024-09-26T17:00:00.000Z",
            //  "description": "",
            //  "location": {
            //    "name": "Mesh Nationaltheatret",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9130126,
            //      "longitude": 10.7341709
            //    },
            //    "@type": "Place",
            //    "@id": "FyAHGzCGYvVljtOszk0rrs",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "KOK",
            //      "url": "https://koknorge.no/",
            //      "@id": "gYQynWJerFZqsVq3m4bWVx",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/a68634d76411defdbe91d825910483c27b309526-1001x1001.png",
            //      "sameAs": [
            //        "https://koknorge.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://koknorge.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Barbara Casique",
            //      "url": "https://oiw.no/speaker/barbara-casique",
            //      "description": "Marketing Manager at KOK, with a rich background in the startup ecosystem, having worked with Startup Norway and Startup Extreme. She’s passionate about building vibrant communities and is now focused on helping Oslo’s innovators connect and thrive. Barbara is excited to lead this event and bring people together for a night of fun and networking.",
            //      "@type": "EducationGroup",
            //      "@id": "jjtTYcZEbCoUqroyk8L4BT",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/74a1e5e096c4df4cfbe193f3b0f945bfc7d2c977-3872x5808.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "mesh-community-stage-kok",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Networking",
            //      "Community",
            //      "Community Stage"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)]  Innovate & Play with a Community Trivia MESH & KOK | Mesh Community Stage Sending event ' Innovate & Play with a Community Trivia MESH & KOK | Mesh Community Stage' to importer
            //{
            //  "name": "Transforming Norway, the 400 billion NOK challenge!",
            //  "url": "https://oiw.no/event/transforming-norway",
            //  "startDate": "2024-09-23T12:00:00.000Z",
            //  "localStartDate": "2024-09-23T14:00",
            //  "endDate": "2024-09-23T14:00:00.000Z",
            //  "description": "",
            //  "location": {
            //    "name": "Mesh Youngstorget",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9139703,
            //      "longitude": 10.7439046
            //    },
            //    "@type": "Place",
            //    "@id": "K7EBmnV6s8FPSv8OiJGQ0I",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Mesh Community ",
            //      "url": "https://meshcommunity.com/",
            //      "@id": "mn1SBBRb6ZAoxkBktzqHA4",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/d48bebc08c7a61f88361875a5e7c738f02ff8126-5178x1601.png",
            //      "sameAs": [
            //        "https://meshcommunity.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://meshcommunity.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Skyfall Ventures",
            //      "url": "https://www.skyfall.vc/",
            //      "@id": "o9fWudzD75M43qA95oSgoa",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/16edcd4e75507f8aaf9ce4f075a470bba139a2ee-700x204.png",
            //      "sameAs": [
            //        "https://www.skyfall.vc/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.skyfall.vc/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Creandum",
            //      "url": "https://creandum.com/",
            //      "@id": "o9fWudzD75M43qA95oSoDw",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/06e298c436f3fba4490ee084264ee5521777872f-908x166.png",
            //      "sameAs": [
            //        "https://creandum.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://creandum.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Earlybird Venture Capital",
            //      "url": "https://earlybird.com/",
            //      "@id": "o9fWudzD75M43qA95oSqLY",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/79eb1fcf3be51bf9648e57a23da7e4feac23ae40-3508x2480.png",
            //      "sameAs": [
            //        "https://earlybird.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://earlybird.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "transforming-norway",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Community",
            //      "Networking"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Transforming Norway, the 400 billion NOK challenge! Sending event 'Transforming Norway, the 400 billion NOK challenge!' to importer
            //{
            //  "name": "Do it now: Embrace change ",
            //  "url": "https://oiw.no/event/apenhet24",
            //  "startDate": "2024-09-25T11:30:00.000Z",
            //  "localStartDate": "2024-09-25T13:30",
            //  "endDate": "2024-09-25T12:30:00.000Z",
            //  "description": "What does adventures teach us? \nWhat makes people able to make big changes? \nWhat information makes us move? \nHow can we inspire others? \n...and how to empower policy makers. \n\nThrough play, games and exercises, we will try to discover protocols of how to embrace change.\n\nJoin the tribe for a guided collaborate and alternative workshop of play to share life experiences. \n\n* At Åpenhet we empower policymakers with actionable data",
            //  "location": {
            //    "name": "Sentralen ",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "@type": "Place",
            //    "@id": "clo3OfcAIt4G4RsnL8bj2a",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Åpenhet",
            //      "url": "https://apenhet.com/",
            //      "@id": "hQLKYFGzcQ7wYQCuKswGPB",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/2954342119774a7abf779022c21d791fb41f3156-1001x263.png",
            //      "sameAs": [
            //        "https://apenhet.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://apenhet.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Kleng Bråtveit",
            //      "url": "https://oiw.no/speaker/kleng-bråtveit",
            //      "description": "Economist, sailor and father. Residing at Ekeberg/Oslo.  ",
            //      "@type": "EducationGroup",
            //      "@id": "PygGVB9TYoPQnjIowG878O",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/423dd356e134c6ca204bd1c8856de58eb7b87c08-2320x3088.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "apenhet24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Community",
            //      "Networking",
            //      "Workshop"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Do it now: Embrace change  Sending event 'Do it now: Embrace change ' to importer
            //{
            //  "name": "From Ukraine to Norway: IT Innovations and Partnership",
            //  "url": "https://oiw.no/event/it-ukraine-association24",
            //  "startDate": "2024-09-24T09:00:00.000Z",
            //  "localStartDate": "2024-09-24T11:00",
            //  "endDate": "2024-09-24T11:00:00.000Z",
            //  "description": "🚀 From Ukraine to Norway: IT Innovations and Partnership\n\nDiscover the future of tech collaboration at this unique two-hour meetup, where the potential of the Ukrainian IT sector meets the innovation-driven Norwegian market. Featuring opening remarks from key representatives, an insightful panel discussion with industry leaders, and a relaxed networking session with food and drinks, this event is a must-attend for anyone looking to tap into cutting-edge innovations in cybersecurity, consumer tech, and emerging technologies.\n\n💡 Why Attend?\n\n- Innovative Insights: Learn how Ukrainian IT companies are driving global success and how their expertise can benefit Norwegian businesses.\n- Expert Connections: Engage with top IT professionals, business leaders, and government representatives to forge meaningful partnerships.\n- Exclusive Networking: Connect with industry players and explore potential collaborations in a relaxed setting.\n\nWhether you aim to expand your business, discover new partners, or stay at the forefront of tech trends, this event is your gateway to the next big wave in IT innovation.\n\n👉 Register now to secure your spot! Don’t miss out on this opportunity to elevate your business with Ukrainian innovation.",
            //  "location": {
            //    "name": "Mesh Youngstorget, Atrium ( Møllergata 6, 8, 0179 Oslo)",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9139676,
            //      "longitude": 10.7464795
            //    },
            //    "@type": "Place",
            //    "@id": "VBlNaGbxeXMiFaeMV30Mcs",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Norwegian-Ukrainian Chamber of Commerce",
            //      "url": "https://nucc.no",
            //      "@id": "j0P7aBo0bkM6HAdsmjge4y",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/4ce11f2e482ffaaf4114334b976b7a3070d21cfe-520x420.png",
            //      "sameAs": [
            //        "https://nucc.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://nucc.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "IT Ukraine Association",
            //      "url": "https://itukraine.org.ua/en/home/",
            //      "@id": "51gVH4x9P9aD66U619PKhD",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/f46baa9dcc42670154493f82ed69e66464c35913-1920x586.png",
            //      "sameAs": [
            //        "https://itukraine.org.ua/en/home/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://itukraine.org.ua/en/home/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Devlight",
            //      "url": "https://devlight.io/?utm_source\u003devent\u0026utm_medium\u003doslo\u0026utm_campaign\u003dinnovation-week",
            //      "@id": "29cfuvScEVHxUPeK7hsbFs",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ef07f4b343be56865ec53474ad17dc0b76ef8037-1123x794.svg",
            //      "sameAs": [
            //        "https://devlight.io/?utm_source\u003devent\u0026utm_medium\u003doslo\u0026utm_campaign\u003dinnovation-week"
            //      ],
            //      "gogo": {
            //        "webpage": "https://devlight.io/?utm_source\u003devent\u0026utm_medium\u003doslo\u0026utm_campaign\u003dinnovation-week"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Nova Digital",
            //      "url": "https://novadigital.com/",
            //      "@id": "VBlNaGbxeXMiFaeMV6WWG2",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/42d98fa07cf9b7cad809c030302bcd899d5d9931-1726x906.png",
            //      "sameAs": [
            //        "https://novadigital.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://novadigital.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Embassy of Ukraine in the Kingdom of Norway",
            //      "url": "https://norway.mfa.gov.ua/en",
            //      "@id": "VJwDF6OhsypgL07DCT0ci8",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/211099d434cb0d09482788787db2bddddeea5d91-1434x492.png",
            //      "sameAs": [
            //        "https://norway.mfa.gov.ua/en"
            //      ],
            //      "gogo": {
            //        "webpage": "https://norway.mfa.gov.ua/en"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Innovation Norway",
            //      "url": "https://en.innovasjonnorge.no/",
            //      "@id": "VJwDF6OhsypgL07DCT0wOc",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ba263ea26bba3fce42df9fd9e6c3fa54b235e567-4481x1953.png",
            //      "sameAs": [
            //        "https://en.innovasjonnorge.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://en.innovasjonnorge.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "IKT Norge",
            //      "url": "https://ikt-norge.no/about-ict-norway/",
            //      "@id": "VBlNaGbxeXMiFaeMV6Wlxy",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/efbbd62061f3733c7a1b1664f8f8779ec8ad4f3d-1917x363.png",
            //      "sameAs": [
            //        "https://ikt-norge.no/about-ict-norway/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://ikt-norge.no/about-ict-norway/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Digital Norway",
            //      "url": "https://digitalnorway.com/prosjekter/about-digital-norway/",
            //      "@id": "C6T6FiNuOtEsdSQR5ck0LH",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/3d7e33316843646aa0bb6ea360ead12bee114aaa-670x163.png",
            //      "sameAs": [
            //        "https://digitalnorway.com/prosjekter/about-digital-norway/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://digitalnorway.com/prosjekter/about-digital-norway/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Ihor Polych",
            //      "url": "https://oiw.no/speaker/ihor-polych",
            //      "description": "- CEO of Devlight\n- Vice President for international cooperation of the Ukrainian IT Association\n- more than 36 million installed applications developed by the Devlight\n- 8 years of experience in IT company management\n- 20 companies from the list of Forbes top 100 companies are clients of Devlight\n- 100+ projects in the company\u0027s portfolio",
            //      "@type": "EducationGroup",
            //      "@id": "29cfuvScEVHxUPeK7hHYB6",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/3dc983528eab914f30a63d2dafc33916868d6bb0-640x835.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Oleh Polihenko",
            //      "url": "https://oiw.no/speaker/oleh-polihenko",
            //      "description": "Ph.D, Chief Information Security Officer (CISO) at Nova Digital (Nova Group), cybersecurity expert with over 10 years of experience, specializing in threat intelligence and penetration testing.",
            //      "@type": "EducationGroup",
            //      "@id": "29cfuvScEVHxUPeK7hIneu",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/44b8808bacdefd83dfb89f30d7ac86efef772537-570x760.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Marius Brendmoe",
            //      "url": "https://oiw.no/speaker/marius-brendmoe",
            //      "description": "Marius has 15+ years of IT and software development experience, specializing in enterprise architecture and leadership. He has driven innovation at Avanade and Funcom. Now, as Head of Digital Transformation at Implenia, Marius modernizes civil engineering through advanced digital solutions, combining technical expertise with a passion for impact.",
            //      "@type": "EducationGroup",
            //      "@id": "VJwDF6OhsypgL07DCSWOQ9",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/f847d81c6e180941397019d7a6eb6c6e082c1826-390x394.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Morten Holst",
            //      "url": "https://oiw.no/speaker/morten-holst",
            //      "description": "Morten, a pioneer in digital media strategy since the dot-com era, has led innovations in mobile, web-TV, and digital storytelling. Currently, he\u0027s a partner at Innocode, focusing on introducing their Smart City platform to international markets with a skilled tech team based in Lviv.",
            //      "@type": "EducationGroup",
            //      "@id": "NO7Mjjgfso2MMqbm3elF4Q",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/f5139c4225e0d0c9616f491e27f8e401a8776f38-1120x1120.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Lema Salamov",
            //      "url": "https://oiw.no/speaker/lema-salamov",
            //      "description": "Lema Salamov has over 10 years of experience in business applications, software development, and solutions architecture. He has contributed to innovation and technical excellence at Avanade, Sopra Steria, and Microsoft. Now, as CEO of LogiqApps AS, Lema leads efforts to deliver cutting-edge digital solutions, empowering businesses to succeed.",
            //      "@type": "EducationGroup",
            //      "@id": "NO7Mjjgfso2MMqbm3fa54O",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/6a97b28ad005830bc794eaae228fbf58fbe0724b-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Nataliya Sjøvoll",
            //      "url": "https://oiw.no/speaker/nataliya-sjøvoll",
            //      "description": "Nataliya Sjøvoll is a Project Manager in the Norwegian-Ukrainian Chamber of Commerce. She has a broad experience working with Norwegian-Ukrainian cooperation in IT seсtor.",
            //      "@type": "EducationGroup",
            //      "@id": "GBgGvFlp21zYJWMNeWVELH",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/0b1f04dcfd92b3209b8d4bea6afcb54eb3a59feb-3380x4732.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Ihor Holovchenko",
            //      "url": "https://oiw.no/speaker/ihor-holovchenko",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "C6T6FiNuOtEsdSQR5jCoTo",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/bb3d8c125df506fc3621fca5783ac87aafcfc838-2239x2564.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Mali Hole Skogen",
            //      "url": "https://oiw.no/speaker/mali-hole-skogen",
            //      "@type": "EducationGroup",
            //      "@id": "6yIAR7qIdwNVpxkGJqvvBT",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/31ddd5f006157734b2089acb2c01969b5d9a6016-800x800.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "it-ukraine-association24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Networking"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] From Ukraine to Norway: IT Innovations and Partnership Sending event 'From Ukraine to Norway: IT Innovations and Partnership' to importer
            //{
            //  "name": "Get Known Startup Comms \u0026 PR Mingle",
            //  "url": "https://oiw.no/event/get-known",
            //  "startDate": "2024-09-26T15:00:00.000Z",
            //  "localStartDate": "2024-09-26T17:00",
            //  "endDate": "2024-09-26T19:00:00.000Z",
            //  "description": "How can a startup grow through media coverage? The Get Known podcast and San Francisco Agency are delighted to invite you to an afternoon of learning, networking, and drinks for marketing, communications, and PR professionals. \n\nJoin us for relaxed networking, insightful conversations, and a chance to connect with and learn from your comms and marketing peers in tech!\n\nWe will be joined by Forbes contributor Daniela De Lorenzo, who will tell you more about how top international media work, what stories they are looking for, and what to avoid when talking to a journalist.",
            //  "location": {
            //    "name": "The Workbar @ MESH Youngstorget",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9139703,
            //      "longitude": 10.7439046
            //    },
            //    "@type": "Place",
            //    "@id": "sjpLtqPJGYKEfVADBAu7Y0",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "San Francisco Agency - Global Growth PR",
            //      "url": "https://sanfrancisco.fi/",
            //      "@id": "Q01A6nB0EzzzgHsi7EKMA3",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/89aa854983be30338cb83fbe7c1506fe7329288e-578x238.png",
            //      "sameAs": [
            //        "https://sanfrancisco.fi/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://sanfrancisco.fi/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Reetta Ilo",
            //      "url": "https://oiw.no/speaker/reetta-ilo",
            //      "description": "Reetta Ilo is the VP of International Growth at San Francisco Agency. She has managed hundreds of PR campaigns for startups looking to expand internationally and built lasting relationships with some of the world\u0027s most prominent tech and business media, such as Forbes, TechCrunch, and Business Insider.",
            //      "@type": "EducationGroup",
            //      "@id": "sjpLtqPJGYKEfVADBAxYIo",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/a90295b1bfb4c694fe318aedcf719bddbdc68995-623x623.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Theodor Salvesen",
            //      "url": "https://oiw.no/speaker/theodor-salvesen",
            //      "description": "Theo is a seasoned comms manager who knows what it takes for a startup to be featured in both top international and Norwegian media. He has previously spoken at events like Startup Extreme, and coached many Norwegian startups in how they can build impactful news stories that reach beyond their own channels and markets.",
            //      "@type": "EducationGroup",
            //      "@id": "Q01A6nB0EzzzgHsi7EMwrl",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/4d3e23d50539a7ed8a8976bc9be3d10a02558b99-2278x2278.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Daniela De Lorenzo",
            //      "url": "https://oiw.no/speaker/daniela-de-lorenzo",
            //      "description": "Daniela De Lorenzo is a reporter who covers breaking news, features and analysis focusing on sustainable food system policy, agritech and foodtech. She’s covered new policy development in the field of cultivated meat. \n\nPreviously, she covered agrifood policies as a staff reporter at Politico Europe from 2022 to 2023. ",
            //      "@type": "EducationGroup",
            //      "@id": "C6T6FiNuOtEsdSQR5gBloP",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/3e120ad4a3a2975259929403519632692dcd4886-830x876.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Per-Ola Mjömark",
            //      "url": "https://oiw.no/speaker/per-ola-mjömark",
            //      "description": "Per-Ola Mjömark is a Senior Comms \u0026 PR Manager at San Francisco Agency.\n\nPer-Ola brings a wealth of experience to the table, specialising in storytelling, securing media coverage, managing PR operations, and training spokespersons. Often with a focus on tech and business, for both international and Swedish media outlets.",
            //      "@type": "EducationGroup",
            //      "@id": "sNzj4K0edIxTY7alYkkaJv",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/bcf55d03d9ff007068adc54d18e5a5bb57f394af-5598x5210.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "get-known",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Networking"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Get Known Startup Comms & PR Mingle Sending event 'Get Known Startup Comms & PR Mingle' to importer
            //{
            //  "name": "Getting real impact bang for your bucks!",
            //  "url": "https://oiw.no/event/impact-startup24",
            //  "startDate": "2024-09-26T06:30:00.000Z",
            //  "localStartDate": "2024-09-26T08:30",
            //  "endDate": "2024-09-26T08:30:00.000Z",
            //  "description": "Investors meet Ferd owner Johan H. Andresen and his social impact investing team. This executive roundtable addresses how to get real measurable impact from investments. \n\nHosted by Impact StartUp, Ferd Social Entrepreneurs and DNB NXT this conversation answers why and how investors should invest in social impact startups. 4 selected social startups present the impact aim to achieve and participate the discussion.  ",
            //  "location": {
            //    "name": "DNB Hovedkontor",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9075534,
            //      "longitude": 10.7598921
            //    },
            //    "@type": "Place",
            //    "@id": "clo3OfcAIt4G4RsnL8DGYw",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Impact StartUp",
            //      "url": "https://www.impactstartup.no/",
            //      "@id": "hQLKYFGzcQ7wYQCuKsVMLp",
            //      "sameAs": [
            //        "https://www.impactstartup.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.impactstartup.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Ferd Social Entrepreneurs ",
            //      "url": "https://ferd.no/en/social-entrepreneurs/who-we-are/",
            //      "@id": "PygGVB9TYoPQnjIowFkFHX",
            //      "sameAs": [
            //        "https://ferd.no/en/social-entrepreneurs/who-we-are/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://ferd.no/en/social-entrepreneurs/who-we-are/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "DNB",
            //      "url": "https://www.dnb.no/",
            //      "@id": "hQLKYFGzcQ7wYQCuKsXURL",
            //      "sameAs": [
            //        "https://www.dnb.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.dnb.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Marte Sootholtet",
            //      "url": "https://oiw.no/speaker/martesootholtet",
            //      "description": "Marte has been deeply committed to entrepreneurship and social innovation throughout her career and is one of Norway\u0027s leading experts on the role of business in social innovation. Marte is the architect behind Impact StartUp and has been a key figure in the company\u0027s development from the beginning.",
            //      "@type": "EducationGroup",
            //      "@id": "VBlNaGbxeXMiFaeMV62fbu",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/0519ba58d5f81a0d10a35931dfd21b35a2f22e1a-3116x4673.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Mari Huuhka Killingmo",
            //      "url": "https://oiw.no/speaker/mari-huuhka-killingmo",
            //      "description": "Mari is responsible for Ferd Social Entrepreneurs deal flow, the area of Financing for Impact and is a business developer for several companies. Mari has an investment background as Investment Director at CapMan Buyout and Senior Adviser in the Ownership Department in the Ministry of Trade, Industry and Fisheries. ",
            //      "@type": "EducationGroup",
            //      "@id": "VJwDF6OhsypgL07DCSMKJV",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/0b0c6bb26ff9948c16d1d28ad27144c3441c77f7-2500x2500.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Astrid Laake Paaske",
            //      "url": "https://oiw.no/speaker/astrid-laake-paaske",
            //      "description": "Astrid is responsible for Impact Measurement and Management in Ferd Social Entrepreneur. \nShe has led several processes to prepare our investments and has been instrumental in further developing the social impact measurement methodology for our team and the companies we work with. She has a background as a consultant in PwC Deals. ",
            //      "@type": "EducationGroup",
            //      "@id": "29cfuvScEVHxUPeK7hnazM",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/da2d00b9bbf91deae00ec21f80b5d7872ba0312b-2500x2500.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Johan H. Andresen",
            //      "url": "https://oiw.no/speaker/johan-h-andresen",
            //      "description": "Johan H. Andresen was the first Norwegian investor to engage in social entrepreneurship and one of the first to use the term in Norway. Since the mid-2000s, he supported several startups and established Ferd Social Entrepreneurs as a new business area within Ferd in 2009. Johan also established Ferd Impact Investing in 2019. ",
            //      "@type": "EducationGroup",
            //      "@id": "VBlNaGbxeXMiFaeMV6525C",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/71b554d0dbf1b1d236477f2642e4dc9a88c6e51d-2560x1708.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "impact-startup24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Impact",
            //      "Investment",
            //      "Workshop"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Getting real impact bang for your bucks! Sending event 'Getting real impact bang for your bucks!' to importer
            //{
            //  "name": "Innovation from the Saga nation!",
            //  "url": "https://oiw.no/event/icelandic-embassy24",
            //  "startDate": "2024-09-26T14:00:00.000Z",
            //  "localStartDate": "2024-09-26T16:00",
            //  "endDate": "2024-09-26T16:00:00.000Z",
            //  "description": "Linkedin - Embassy of Iceland in Norway, Oslo\nFacebook - Islands ambassade i Oslo / Sendiráð Íslands í Osló",
            //  "location": {
            //    "name": "Icelandic Embassy\u0027s Residence",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": " 0286 Oslo",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9037551,
            //      "longitude": 10.6850224
            //    },
            //    "@type": "Place",
            //    "@id": "1OTPBbxLi7RhNZW31HaCDG",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Icelandic embassy in Oslo",
            //      "url": "https://www.government.is/default.aspx?pageid\u003daec5318c-ee36-485f-8459-334883363693\u0026",
            //      "@id": "ZPMzi4WsP864CshknbFdL9",
            //      "sameAs": [
            //        "https://www.government.is/default.aspx?pageid\u003daec5318c-ee36-485f-8459-334883363693\u0026"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.government.is/default.aspx?pageid\u003daec5318c-ee36-485f-8459-334883363693\u0026"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "KLAK - Icelandic startups",
            //      "url": "https://klak.is/",
            //      "@id": "ZNmtd2wLKytKRL8gA5r28w",
            //      "sameAs": [
            //        "https://klak.is/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://klak.is/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Iceland Innovation Week",
            //      "url": "https://www.innovationweek.is/",
            //      "@id": "NwD6lUuZcgi96yANLBY2o4",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/828aa3ba259ea819786de59bf121a06517731702-1000x1000.png",
            //      "sameAs": [
            //        "https://www.innovationweek.is/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.innovationweek.is/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Icelandair",
            //      "url": "https://www.icelandair.com/no-no/",
            //      "@id": "NwD6lUuZcgi96yANLBY46C",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/4e168335915337f5e614cd4c477f4b73ec250335-800x209.png",
            //      "sameAs": [
            //        "https://www.icelandair.com/no-no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.icelandair.com/no-no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Nordic Ignite",
            //      "url": "https://www.nordicignite.com/",
            //      "@id": "iwGHI4I44KMT0cEvxDfj4t",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/9d6c9c07220fd6af771a1d3eb5610b2615c669e4-412x467.png",
            //      "sameAs": [
            //        "https://www.nordicignite.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.nordicignite.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "SDG/TBWA",
            //      "@id": "iwGHI4I44KMT0cEvxDfp4V",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/29196651419a9789d33d7608d36086a2cba679d4-457x77.png",
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Norsk-islandsk Handelskammer",
            //      "url": "https://norsk-islenska.is/",
            //      "@id": "xBB7iBjw6W7EaSR6vDnUBs",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/f0f1c1f94fb4630386bcb5ca1b114c7bf38ee080-96x78.png",
            //      "sameAs": [
            //        "https://norsk-islenska.is/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://norsk-islenska.is/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Edda Konráðsdóttir",
            //      "url": "https://oiw.no/speaker/edda-konráðsdóttir",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "NwD6lUuZcgi96yANLBYpuy",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ec6a841ba0cdc4f10e49a873d78ba7820948904a-500x666.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Sigurður Árnason",
            //      "url": "https://oiw.no/speaker/sigurður-árnason",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "iwGHI4I44KMT0cEvxDhUXz",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/7db48a9008474869bfa2c6f2c59186affd8ed699-1363x2048.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Bjarni Herrera",
            //      "url": "https://oiw.no/speaker/bjarni-herrera",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "brPasVeoHS59tFoOaRYEuX",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/0fcb15c259c0f98303106eef870a3096077ba810-1600x2400.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Thorey V. Proppe",
            //      "url": "https://oiw.no/speaker/thorey-v-proppe_",
            //      "@type": "EducationGroup",
            //      "@id": "brPasVeoHS59tFoOaRtc8P",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/80d47dc6064e0ecf4f7965dacb25e7010c0aa8b4-2000x1334.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "icelandic-embassy24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Networking",
            //      "Investment"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Innovation from the Saga nation! Sending event 'Innovation from the Saga nation!' to importer
            //{
            //  "name": "Propfest - The Official OIW Afterparty",
            //  "url": "https://oiw.no/event/proptech-summit-24-afterpary",
            //  "startDate": "2024-09-25T16:00:00.000Z",
            //  "localStartDate": "2024-09-25T18:00",
            //  "endDate": "2024-09-26T00:00:00.000Z",
            //  "description": "https://www.linkedin.com/company/proptech-norway/",
            //  "location": {
            //    "name": "Pakkhuset Oslo",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9094251,
            //      "longitude": 10.7401382
            //    },
            //    "@type": "Place",
            //    "@id": "j0P7aBo0bkM6HAdsmhvPoG",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Proptech Norway",
            //      "url": "https://www.proptechnorway.co/",
            //      "@id": "udBHoLbJZaALmUA1TrBBej",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/4fdbc5b7137eb4b5c9fdffbe7b6276a711e275fa-577x229.png",
            //      "sameAs": [
            //        "https://www.proptechnorway.co/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.proptechnorway.co/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "proptech-summit-24-afterpary",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Afterparty",
            //      "Networking"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Propfest - The Official OIW Afterparty Sending event 'Propfest - The Official OIW Afterparty' to importer
            //{
            //  "name": "lululemon x Mesh – Morning Run with Allan Daniel Corona",
            //  "url": "https://oiw.no/event/lululemon-x-mesh-morning-run-with-allan-daniel-corona",
            //  "startDate": "2024-09-25T06:30:00.000Z",
            //  "localStartDate": "2024-09-25T06:30:00.000Z",
            //  "endDate": "2024-09-25T07:30:00.000Z",
            //  "location": {
            //    "name": "Lululemon (Karl Johans gate 23)",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9128912,
            //      "longitude": 10.7400882
            //    },
            //    "@type": "Place",
            //    "@id": "2bd6a3ca-d542-4e4c-967d-47b83a72dbe8",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Mesh Community",
            //      "url": "https://meshcommunity.com/",
            //      "@id": "HJ5uo4LMcm2DGW55qrGO6E",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/d48bebc08c7a61f88361875a5e7c738f02ff8126-5178x1601.png",
            //      "sameAs": [
            //        "https://meshcommunity.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://meshcommunity.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "lululemon-x-mesh-morning-run-with-allan-daniel-corona",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Community",
            //      "Sport"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] lululemon x Mesh – Morning Run with Allan Daniel Corona Sending event 'lululemon x Mesh – Morning Run with Allan Daniel Corona' to importer
            //{
            //  "name": "Impact Takes Center Stage ",
            //  "url": "https://oiw.no/event/she24",
            //  "startDate": "2024-09-24T13:00:00.000Z",
            //  "localStartDate": "2024-09-24T15:00",
            //  "endDate": "2024-09-24T14:30:00.000Z",
            //  "description": "Impact Takes Center Stage at Oslo Innovation Week with EY and SHE Conference.\n \nLet\u0027s merge innovation with conscience! Discover how top Private Equity firms deploy ESG- and impact strategies to derive both financial and societal value. Do you want to learn more about the transformative potential of private capital and how to become attractive to investors? Join us! \n \nThis event is tailored for those seeking to align their vision with purpose and who would like to be part of the change where impact enhances performance. \n \nThis event is tailored for those seeking to align their vision with purpose and who would like to be part of the change where impact aligns with performance.\n\nAfter the event, you can enjoy the social gathering at the rooftop bar.",
            //  "location": {
            //    "name": "S7 - EY",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": " 0155 Oslo",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9133086,
            //      "longitude": 10.7460834
            //    },
            //    "@type": "Place",
            //    "@id": "3bSeV1kXTHmPOpL580J0Va",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "SHE Conference",
            //      "url": "https://www.sheconference.no",
            //      "@id": "pGegU7xtOJjMtC6RbsZwne",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/5a8e78d7dd0e5b110b7331dd4814b4118727d2ca-1080x1080.png",
            //      "sameAs": [
            //        "https://www.sheconference.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.sheconference.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "EY",
            //      "url": "https://www.ey.com/no_no",
            //      "@id": "pGegU7xtOJjMtC6RbsaiPS",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/7e8020ca3b501a6845e873631e054826fecd7faf-1080x1080.png",
            //      "sameAs": [
            //        "https://www.ey.com/no_no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.ey.com/no_no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Jan Henry S. Fosse",
            //      "url": "https://oiw.no/speaker/jan-henry-s-fosse",
            //      "description": "Jan Henry is a partner at EY-Parthenon, the global strategy practice of EY based in our Oslo office. He leads the Nordic sustainability strategy and transactions practice and is also part of our energy transition and resource efficiency practices, in addition to experience across food, TMT and healthcare. ",
            //      "@type": "EducationGroup",
            //      "@id": "IB3GRND3P7nAlGsNFwYaqH",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/f8a40c6261d62f5c8a44ebe9b3fc901b2bdcb99b-1080x1080.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Astrid Rønning Skaugseth",
            //      "url": "https://oiw.no/speaker/astrid-rønning-skaugseth",
            //      "description": "Astrid R. Skaugseth is the CEO of SHE Conference (Social Human Equity). She has spent most of her career in EY in positions such as COO at EY Norway, COO at Tax \u0026 Law Nordics, and as the EY Nordics Sustainability Lead. ",
            //      "@type": "EducationGroup",
            //      "@id": "fqonTtodcrOOdoYy4KCF3K",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/f8e185224fe1abe53b7f73bf6e51f99bb4e1281c-1080x1080.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Linn Anker-Sørensen",
            //      "url": "https://oiw.no/speaker/linn-anker-sørensen",
            //      "description": "Linn has followed the development of sustainability law since 2010 as a legal researcher and advises clients in strategic orientation, disclosure regimes, sustainable corporate governance, supply chain due diligence and evaluation of green investment and asset ratios.\nLinn holds a PhD in Law. ",
            //      "@type": "EducationGroup",
            //      "@id": "fwluKSLh9gfPaX1JBuC3iC",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/0b8f7e38084e336e3eb5ccb5e0cb7645a204d6d7-1080x1080.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Michelle Chinnappen",
            //      "url": "https://oiw.no/speaker/michelle-chinnappen",
            //      "description": "Michelle Chinnappen is an experienced business builder, communicator, facilitator, public speaker, and team player.  As a strategic advisor she has lead processes and projects in branding, marketing, creating efficient work processes, workshop facilitation, leadership training and strategic implementing etc.",
            //      "@type": "EducationGroup",
            //      "@id": "LxW2c7ek5iDOrc2j9UyxWQ",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/0edd4fff282984ad16d9065afbed4e384c3f5c4a-1080x1080.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Ellen Nyhus",
            //      "url": "https://oiw.no/speaker/ellen-nyhus",
            //      "description": "Ellen joined Verdane as the CEO of Elevate in 2022. Previously to joining Verdane, Ellen has had multiple roles as CEO and Human Resources Executive in the technology, software, and professional services industry.",
            //      "@type": "EducationGroup",
            //      "@id": "JxpKUxUWSDPhcfPlpa2pR4",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/1e55874199ecb37c9332fc49c6b6ab843042139a-1080x1080.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Marianne Wangen-Eriksen",
            //      "url": "https://oiw.no/speaker/marianne-wangen-eriksen",
            //      "@type": "EducationGroup",
            //      "@id": "JxpKUxUWSDPhcfPlpa2vbY",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/57d19b3420952b031bef639ccfbe58cbe64bbfdf-1080x1080.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Jonas Kjellberg",
            //      "url": "https://oiw.no/speaker/jonas-kjellberg",
            //      "description": "Jonas is a serial entrepreneur, disruptor, investor, and game changer. He is the Founder and Chairman of the board at NORNORM - a Nordic scale-up company with a fully circular business model that provides office furniture as a service.",
            //      "@type": "EducationGroup",
            //      "@id": "Kr5dD2L5cV66ZmRYNu2WzC",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/df085b2da2b77e8f0cf2977809dd7b3a285ed2d2-1080x1080.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "she24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Impact",
            //      "Networking",
            //      "Panel"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Impact Takes Center Stage  Sending event 'Impact Takes Center Stage ' to importer
            //{
            //  "name": "🧘‍♀️ Bruce Studios x Mesh : Yoga/Breathwork Experience",
            //  "url": "https://oiw.no/event/bruce-x-mesh-yoga-breathwork-experience",
            //  "startDate": "2024-09-23T05:00:00.000Z",
            //  "localStartDate": "2024-09-23T07:00",
            //  "endDate": "2024-09-23T06:00:00.000Z",
            //  "location": {
            //    "name": "Mesh Youngstorget",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9139676,
            //      "longitude": 10.7464795
            //    },
            //    "@type": "Place",
            //    "@id": "z3L3xjQSUzI9kbu7ZPEWHk",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Bruce Studios ",
            //      "url": "https://www.brucestudios.com",
            //      "@id": "Z1HRcEL2TVXt8YyyjheLT4",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/007e4eaa33d6fb7954b4300689af8ef79744ce11-400x400.png",
            //      "sameAs": [
            //        "https://www.brucestudios.com"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.brucestudios.com"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "bruce-x-mesh-yoga-breathwork-experience",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Fitness \u0026 Wellbeing",
            //      "Sport"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] 🧘‍♀️ Bruce Studios x Mesh : Yoga/Breathwork Experience Sending event '🧘‍♀️ Bruce Studios x Mesh : Yoga/Breathwork Experience' to importer
            //{
            //  "name": "Green Morning with the EEA and Norway Grants ",
            //  "url": "https://oiw.no/event/eea-and-norway-grants24",
            //  "startDate": "2024-09-25T07:00:00.000Z",
            //  "localStartDate": "2024-09-25T09:00",
            //  "endDate": "2024-09-25T10:30:00.000Z",
            //  "description": "Innovation Norway and partners invites you to a green morning! Focusing on circular economy, this networking event will allow you to explore circular business possibilities and meet potential business partners from across Europe! ",
            //  "location": {
            //    "name": "Grev Wedels Plass 9",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "@type": "Place",
            //    "@id": "xBB7iBjw6W7EaSR6vDWR0S",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Innovation Norway ",
            //      "url": "https://www.innovasjonnorge.no/",
            //      "@id": "27jkfP9EFXFTG07fKbJwDs",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ba263ea26bba3fce42df9fd9e6c3fa54b235e567-4481x1953.png",
            //      "sameAs": [
            //        "https://www.innovasjonnorge.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.innovasjonnorge.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "EEA and Norway Grants ",
            //      "url": "https://eea.innovationnorway.com/",
            //      "@id": "27jkfP9EFXFTG07fKbJxim",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/335ed7cc5680e9590af43eb51151de00634fa624-715x294.png",
            //      "sameAs": [
            //        "https://eea.innovationnorway.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://eea.innovationnorway.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "eea-and-norway-grants24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Sustainability",
            //      "Networking",
            //      "Business Conference"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Green Morning with the EEA and Norway Grants  Sending event 'Green Morning with the EEA and Norway Grants ' to importer
            //{
            //  "name": "Oslo Innovation Week x The Conduit: Building a Better Future Celebrating Pioneering Sustainable Solutions",
            //  "url": "https://oiw.no/event/building-a-better-future24",
            //  "startDate": "2024-09-24T16:00:00.000Z",
            //  "localStartDate": "2024-09-24T18:00",
            //  "endDate": "2024-09-24T21:00:00.000Z",
            //  "description": "Indulge in refreshing drinks, and engaging conversations in an atmosphere buzzing with creativity and inspiration. Don\u0027t miss out on this unique opportunity to connect, collaborate, and celebrate the pioneers at The Conduit.",
            //  "location": {
            //    "name": "The Conduit ",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9171638,
            //      "longitude": 10.7339552
            //    },
            //    "@type": "Place",
            //    "@id": "75hF6u0k3iHYTKl66PZfL8",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "The Conduit ",
            //      "url": "https://oslo.theconduit.com/",
            //      "@id": "FyAHGzCGYvVljtOszl8vae",
            //      "sameAs": [
            //        "https://oslo.theconduit.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oslo.theconduit.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "building-a-better-future24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Afterparty",
            //      "Networking"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Oslo Innovation Week x The Conduit: Building a Better Future Celebrating Pioneering Sustainable Solutions Sending event 'Oslo Innovation Week x The Conduit: Building a Better Future Celebrating Pioneering Sustainable Solutions' to importer
            //{
            //  "name": "Make the Norwegian Model great again",
            //  "url": "https://oiw.no/event/abelia24",
            //  "startDate": "2024-09-23T15:00:00.000Z",
            //  "localStartDate": "2024-09-23T17:00",
            //  "endDate": "2024-09-23T16:00:00.000Z",
            //  "description": "The Norwegian model has served us well. How does it set up entrepreneurs for success? ",
            //  "location": {
            //    "name": "Becco vinbar",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": " Loftet",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "@type": "Place",
            //    "@id": "BFlh10fxnloOFB5KgXRas7",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Abelia",
            //      "url": "https://www.abelia.no/",
            //      "@id": "1O6UfVeEhrCXNCgCQdxm0M",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/42322e28364e8734e5b86e216312c416e2d34e6e-10647x3258.png",
            //      "sameAs": [
            //        "https://www.abelia.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.abelia.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Tekna",
            //      "url": "https://www.tekna.no/",
            //      "@id": "uifTdwBj65JXb9pObqJSSq",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/9c9e3a3e80e774ea37a2601998d2bcf7ac39dc71-1552x456.png",
            //      "sameAs": [
            //        "https://www.tekna.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.tekna.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Harald Eia",
            //      "url": "https://oiw.no/speaker/harald-eia",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "1O6UfVeEhrCXNCgCQr5jfw",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/6a1591ec5e40d6fddd543e9dea8c8a44bc6330d9-4821x3214.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Elisabet Haugsbø",
            //      "url": "https://oiw.no/speaker/elisabet-haugsbø",
            //      "@type": "EducationGroup",
            //      "@id": "L1QlNrpdCbsRFieBF8PVbM",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/99183a6e7bf97f6c969f69779c732d92982e5d72-1920x1280.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Kristin Rotevatn Nyberg",
            //      "url": "https://oiw.no/speaker/kristin-rotevatn-nyberg",
            //      "@type": "EducationGroup",
            //      "@id": "JxpKUxUWSDPhcfPlpZeX0H",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/a35358be6a3ef6aad7dc093f6b52726186ac47e6-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Merete Nygaard",
            //      "url": "https://oiw.no/speaker/merete-nygaard",
            //      "@type": "EducationGroup",
            //      "@id": "vd41e4YzH4Vh3UmcvsqdOa",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/781c09abf790515487b83dea62cd1742b77e7306-300x300.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Ingvild von Krogh Strand",
            //      "url": "https://oiw.no/speaker/ingvild-von-krogh-strand",
            //      "@type": "EducationGroup",
            //      "@id": "2Zk6tWckGuE3g8LyMKurbY",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/56c5682b3cd68f5811110a822cb2245112fa24d1-240x320.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Fernanda Winger Eggen",
            //      "url": "https://oiw.no/speaker/fernanda-winger-eggen",
            //      "@type": "EducationGroup",
            //      "@id": "1VsbhneYEYwg8EYOpUTgCg",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/feb98414dc5675cd02cb7526203c6c0095d8fe19-240x320.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "abelia24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Sustainability",
            //      "Panel"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Make the Norwegian Model great again Sending event 'Make the Norwegian Model great again' to importer
            //{
            //  "name": "How to secure financing from leading international VCs",
            //  "url": "https://oiw.no/event/how-to-secure-financing-from-leading-international-vcs24",
            //  "startDate": "2024-09-25T14:00:00.000Z",
            //  "localStartDate": "2024-09-25T16:00",
            //  "endDate": "2024-09-25T15:00:00.000Z",
            //  "description": "nFront and a group of leading international VCs are excited to invite you to an interactive session on how to navigate the processes of venture fundraising.",
            //  "location": {
            //    "name": "Youngs Oslo",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "@type": "Place",
            //    "@id": "LxW2c7ek5iDOrc2j9TOTXw",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "nFront Ventures",
            //      "url": "https://nfrontventures.com",
            //      "@id": "PygGVB9TYoPQnjIowDH0yB",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/a17406e6ff8ac4caf2bcece8bd4f526fd2d2c4aa-2160x2160.png",
            //      "sameAs": [
            //        "https://nfrontventures.com"
            //      ],
            //      "gogo": {
            //        "webpage": "https://nfrontventures.com"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Eight Roads",
            //      "url": "https://eightroads.com/en",
            //      "@id": "219Yk5zrke4rIaZuIAKfyX",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/033d4fb9cc4b434105048b3254e47558e8e0e493-1541x109.png",
            //      "sameAs": [
            //        "https://eightroads.com/en"
            //      ],
            //      "gogo": {
            //        "webpage": "https://eightroads.com/en"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Atomico",
            //      "url": "https://atomico.com",
            //      "@id": "cCCayfaKc46JzZktBrXaTr",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/63c7f95a48d46a38c077d132dbfbf3bb26348648-868x172.png",
            //      "sameAs": [
            //        "https://atomico.com"
            //      ],
            //      "gogo": {
            //        "webpage": "https://atomico.com"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Peak",
            //      "url": "https://peak.capital",
            //      "@id": "2FHCi87pvX3wrqgdPDcviY",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/69f9a5abc4830a7b82d3ce74d373f6ae5c50dd49-401x401.png",
            //      "sameAs": [
            //        "https://peak.capital"
            //      ],
            //      "gogo": {
            //        "webpage": "https://peak.capital"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Magnus Gaarder",
            //      "url": "https://oiw.no/speaker/magnus-gaarder",
            //      "description": "Magnus is a technology-focused venture capitalist, with a background in investment banking and computer science. As the Founding Partner of nFront, Magnus brings operational support and capital to companies across various stages of development. nFront’s focus is on game-changing, capital efficient, software propositions based in Europe or the U.S. ",
            //      "@type": "EducationGroup",
            //      "@id": "PygGVB9TYoPQnjIowDH1oY",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/bb848aeddf9250917ae8af1f7a5e1de2c3ff74b4-512x512.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Amanda Hultman",
            //      "url": "https://oiw.no/speaker/amanda-hultman",
            //      "description": "Amanda works at London-based VC firm Atomico. She sits in the early stage team and is primarily focused on Series A investments in the Nordics and Baltics.",
            //      "@type": "EducationGroup",
            //      "@id": "219Yk5zrke4rIaZuIAKN49",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/8e0e17dc83c2ae46df6241cfaf2bec4b6eb5b576-200x200.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Martin Ericsson",
            //      "url": "https://oiw.no/speaker/martin-ericsson",
            //      "description": "Martin is an investor in the Eight Roads Ventures Europe team. Originally from Sweden, Martin leads Eight Roads Ventures efforts in the Nordics and is focused on sourcing new investments, lead new investments and working with existing portfolio companies to help them scale.",
            //      "@type": "EducationGroup",
            //      "@id": "4LmnWJ6svv3AwRfuxB42RL",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/e02d30f6d2ea1398a12c6f23e93c7e14a16b4de7-706x706.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Felicia Nordgård",
            //      "url": "https://oiw.no/speaker/felicia-nordstrom",
            //      "description": "Felicia is one of Peak’s Stockholm based Partners, covering the Nordics and investing in all SaaS, marketplace and platform related. She comes from the finance world, turned venture builder and ended up in VC. Growing up she competed for the Swedish national team of rhythmic gymnastics, also contributing to her interest in health and social impact.",
            //      "@type": "EducationGroup",
            //      "@id": "cILG734b7Geyfqvxx1qnSv",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/5407f11de0f602f88ee0ba3593a19eca66c494e5-2337x2337.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "how-to-secure-financing-from-leading-international-vcs24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Investment",
            //      "Panel"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] How to secure financing from leading international VCs Sending event 'How to secure financing from leading international VCs' to importer
            //{
            //  "name": "Founder or Female Founder?",
            //  "url": "https://oiw.no/event/lapakko24",
            //  "startDate": "2024-09-25T15:30:00.000Z",
            //  "localStartDate": "2024-09-25T17:30",
            //  "endDate": "2024-09-25T17:00:00.000Z",
            //  "description": "Bring your passion for gender equality to this #OIW2024 workshop. We\u0027ll collaborate, share our experiences, and establish clear goals for initiatives around how to empower female founders in Hokkaido, Japan—the country that ranks 118th in gender equality.",
            //  "location": {
            //    "name": "Atelie",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9121548,
            //      "longitude": 10.7238719
            //    },
            //    "@type": "Place",
            //    "@id": "CmlaH7ihJVXUEwGX2EU3jP",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Hokkaido Innovation Week",
            //      "url": "https://innovationweek.jp/",
            //      "@id": "bJU0rnud4tYDJnYMKN0zIo",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/22228bb302df45249b2d8ca5c4091627b20a83b5-1346x1346.png",
            //      "sameAs": [
            //        "https://innovationweek.jp/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://innovationweek.jp/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Lumo Labs ",
            //      "url": "https://lumolabs.io/",
            //      "@id": "vd41e4YzH4Vh3UmcvsPHlk",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/08bd952163ce79e7bbc3e138756979ab30944528-788x788.png",
            //      "sameAs": [
            //        "https://lumolabs.io/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://lumolabs.io/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "AWAN: As We Are Now",
            //      "url": "https://aswearenow.co",
            //      "@id": "Kr5dD2L5cV66ZmRYNtpQqA",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/bc0085c3af5f92009a7c76b489a2d815c4313e93-225x225.png",
            //      "sameAs": [
            //        "https://aswearenow.co"
            //      ],
            //      "gogo": {
            //        "webpage": "https://aswearenow.co"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "feminvest",
            //      "url": "https://feminvest.se/en/",
            //      "@id": "vd41e4YzH4Vh3UmcvsPJDQ",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/22aed206f07134043cefcc05e2cd5c0d92e4719f-1370x1364.png",
            //      "sameAs": [
            //        "https://feminvest.se/en/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://feminvest.se/en/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Miho Tanaka",
            //      "url": "https://oiw.no/speaker/miho-tanaka",
            //      "description": "Diversifying startup ecosystems with international founders by designing governmental initiatives to bridge governments, local communities, and global founders.",
            //      "@type": "EducationGroup",
            //      "@id": "FNKpwkKRimvSChfWMwL5sB",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/a1cc72f71d7d98205fdadb294912768e9eda50af-480x480.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Linn-Cecilie Linnemann",
            //      "url": "https://oiw.no/speaker/linn-cecilie-linnemann",
            //      "description": "Linn-Cecilie Linnemann is a seasoned serial entrepreneur and investor, with a track record of over 20 years in business strategy, investment, and communication. With a keen eye for innovation and experience as an active seed investor and board member, Linnemann has significantly contributed to the growth and success of various ventures.",
            //      "@type": "EducationGroup",
            //      "@id": "i25yJA0neSgT1BpxaID8hb",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/f466442883529e63239d96f715eb99c0c14ad8dd-400x400.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Anette Miwa Dimmen",
            //      "url": "https://oiw.no/speaker/anette-miwa-dimmen1",
            //      "description": "Anette is the Founder \u0026 CEO of AWAN: As We Are Now. AWAN is the only Norwegian fashion brand in the Antler VC portfolio and currently raising our pre-seed round, and it is on a mission to merge fashion with wellness, closing the sustainability gap between the clothes that we buy and the clothes that we actually wear. \n\n",
            //      "@type": "EducationGroup",
            //      "@id": "vd41e4YzH4Vh3UmcvsPNL0",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/f7bbf56c0f6b49aeef08e5308a04a0a58cd19b34-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Sara Sorbet",
            //      "url": "https://oiw.no/speaker/sara-sorbet",
            //      "description": "Her passion in life is communication and sustainable relations. Through her life she have helped, and still helping two start ups to build and grow with sales and events. Her main goal has been to establish newly launched products, establish them and make the sales numbers shoot through the roof.",
            //      "@type": "EducationGroup",
            //      "@id": "vd41e4YzH4Vh3UmcvsPSKO",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/232d1263f8829e2f77fb1ec4a6eed7900278bd3d-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "lapakko24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Impact",
            //      "Community",
            //      "Workshop"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Founder or Female Founder? Sending event 'Founder or Female Founder?' to importer
            //{
            //  "name": "Transforming Tensions into Opportunities",
            //  "url": "https://oiw.no/event/we-are-human24",
            //  "startDate": "2024-09-25T09:00:00.000Z",
            //  "localStartDate": "2024-09-25T11:00",
            //  "endDate": "2024-09-25T09:40:00.000Z",
            //  "description": "Join us for an exclusive preview of We Are Human\u0027s new \"in development\" framework, designed to steer businesses toward a regenerative future by transforming organizational tensions into strategic opportunities—spaces are limited!",
            //  "location": {
            //    "name": "Mesh Youngstorget",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "@type": "Place",
            //    "@id": "3bSeV1kXTHmPOpL580Kere",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "We Are Human",
            //      "url": "https://www.wearehuman.cc/",
            //      "@id": "69rK5Hd9X3fjRReZJwR9vw",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/1fdcf567d78a515610dd190846e8418c17f1a7db-225x225.png",
            //      "sameAs": [
            //        "https://www.wearehuman.cc/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.wearehuman.cc/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Johan Brand",
            //      "url": "https://oiw.no/speaker/johan-brand",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "mn1SBBRb6ZAoxkBktzqJBg",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/bdf3d9399200bb4c01cd6e94aa93291b13110eb5-1400x750.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Jamie Brooker",
            //      "url": "https://oiw.no/speaker/jamie-brooker",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "69rK5Hd9X3fjRReZJwRHB4",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/e3c4710dbbe7cbeeb26b86986349f8b0e72fa35f-772x772.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Marta Sjögren",
            //      "url": "https://oiw.no/speaker/marta-sjögren",
            //      "@type": "EducationGroup",
            //      "@id": "VBlNaGbxeXMiFaeMV6B4c6",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/fe84ce17c8da40d98ae8cad3a28d5de028730b78-957x1000.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Marie Ekeland",
            //      "url": "https://oiw.no/speaker/marie-ekeland",
            //      "@type": "EducationGroup",
            //      "@id": "VBlNaGbxeXMiFaeMV6BDfe",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/5f5159df8b1c19140c62f0caaa0f37bef597bb5c-937x720.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "we-are-human24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "ClimateTech",
            //      "Impact",
            //      "Panel"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Transforming Tensions into Opportunities Sending event 'Transforming Tensions into Opportunities' to importer
            //{
            //  "name": "👟 Bruce Studios x Mesh: Team Spirit Run",
            //  "url": "https://oiw.no/event/bruce-x-mesh-team-spirit-run",
            //  "startDate": "2024-09-25T05:30:00.000Z",
            //  "localStartDate": "2024-09-25T07:30",
            //  "endDate": "2024-09-25T06:15:00.000Z",
            //  "location": {
            //    "name": "Mesh Nationaltheatret",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9130126,
            //      "longitude": 10.7341709
            //    },
            //    "@type": "Place",
            //    "@id": "V1xQNSx4THHcAANnGqZHeC",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Bruce Studios",
            //      "url": "https://www.brucestudios.com",
            //      "@id": "sjpLtqPJGYKEfVADBAs3I4",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/007e4eaa33d6fb7954b4300689af8ef79744ce11-400x400.png",
            //      "sameAs": [
            //        "https://www.brucestudios.com"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.brucestudios.com"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "bruce-x-mesh-team-spirit-run",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Fitness \u0026 Wellbeing",
            //      "Sport"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] 👟 Bruce Studios x Mesh: Team Spirit Run Sending event '👟 Bruce Studios x Mesh: Team Spirit Run' to importer
            //{
            //  "name": "Diversify Nordics Summit",
            //  "url": "https://oiw.no/event/diversify-nordics-summit24",
            //  "startDate": "2024-09-27T05:30:00.000Z",
            //  "localStartDate": "2024-09-27T07:30",
            //  "endDate": "2024-09-27T15:00:00.000Z",
            //  "description": "The Diversify Nordics Summit (DNS) is the leading conference for leadership, innovation, and sustainable change in the Nordics and Europe, attracting leaders and professionals from diverse industries and backgrounds.\n\nCrowned as the most intersectionally inclusive conference in Northern Europe, DNS is the principal platform to broaden your professional network, gain practical insights and secure lasting partnerships.",
            //  "location": {
            //    "name": "Clarion Hotel, The Hub",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9125358,
            //      "longitude": 10.7474435
            //    },
            //    "@type": "Place",
            //    "@id": "sniusGS60fAYlkDAg5jO2e",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Diversify",
            //      "url": "https://diversify.no",
            //      "@id": "sniusGS60fAYlkDAg5jTc8",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/1938d0cd90893d9b2df7e7c7c7438f52c3a4d9cc-131x34.svg",
            //      "sameAs": [
            //        "https://diversify.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://diversify.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Chisom Udeze",
            //      "url": "https://oiw.no/speaker/chisom-udeze",
            //      "description": "Chisom is a seasoned economist and three-time founder. At Diversify and Diversify Consult, she spearheads the development of sustainable DEIB strategies for companies, governments, and civil society. Notably, Diversify launched the Diversify Nordics Summit in 2022, a significant conference advancing DEIB in the Nordics. ",
            //      "@type": "EducationGroup",
            //      "@id": "oFjgeYIjUFByKt1TzAXnYW",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/7a3d1548467ab54ede93843996a12c38d5cb8ebb-800x800.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Tine Austvoll Jensen",
            //      "url": "https://oiw.no/speaker/tine-austvoll-jensen",
            //      "description": "Country Director of Google Norway and member of the regional board Northern Europe. Board member Norwegian Handball Federation and Sheconomy. Tine has 20+ years experience within TV, media and tech. She joined Google from her previous position as CEO and Editor in Chief of Discovery Norway. She has international experience from Malaysia, Thailand, ",
            //      "@type": "EducationGroup",
            //      "@id": "IB3GRND3P7nAlGsNFxwZ0q",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/03be39263e22ec6cc7225b3e2e20eff9ee2cfe18-500x500.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Momodou Malcolm Jallow",
            //      "url": "https://oiw.no/speaker/momodou-malcolm-jallow",
            //      "description": "Momodou Malcolm Jallow is currently a member of the Swedish parliament and Chair of the standing Committee on Civil Affairs. He is a former member of the Parliamentary Assembly of the Council of Europe and Vice-Chair of its Committee on Equality and Non-Discrimination.",
            //      "@type": "EducationGroup",
            //      "@id": "IB3GRND3P7nAlGsNFxwsO3",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/f7e12ee905161e1ba2e18ee2741ca809c9014c77-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Dr. Poornima Luthra",
            //      "url": "https://oiw.no/speaker/dr-poornima-luthra",
            //      "description": "Dr. Poornima Luthra is a recognized author, keynote speaker, facilitator/trainer, consultant, and leading practitioner-academic in the field of talent management and Diversity, Equity, and Inclusion (DEI). She is the author of Leading Through Bias, The Art of Active Allyship and Diversifying Diversity.",
            //      "@type": "EducationGroup",
            //      "@id": "o9fWudzD75M43qA95oK0ls",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/315fd7f48ddc10795ca591d34828d04c8037d404-575x575.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Lori George",
            //      "url": "https://oiw.no/speaker/lori-george",
            //      "description": "Lori George is a Fortune 500 Director and currently serves on the Board of Directors of Pioneer Natural Resources (NYSE: PXD) and Shake Shack (NYSE: SHAK). She is on the Nominating Governance, Health, Safety and Environment and Compensation and Leadership Development Committees.\n",
            //      "@type": "EducationGroup",
            //      "@id": "IB3GRND3P7nAlGsNFxxMfT",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/39d7440833904d86ed8231d4ce9d585db54b7775-670x694.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Wenche Fredriksen",
            //      "url": "https://oiw.no/speaker/wenche-fredriksen",
            //      "description": "Wenche Fredriksen is the Head of Inclusion \u0026 Diversity and at DNB. She has worked at Accenture for 30 years, having roles within consulting, HR and as Nordic Inclusion \u0026 Diversity. Wenche is an inspirational storyteller who actively uses her life experiences to open people’s hearts and minds, and to build a more diverse and inclusive world.",
            //      "@type": "EducationGroup",
            //      "@id": "fqonTtodcrOOdoYy4LqZ6g",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/fd742232c248e870afd9f724e4c26e100b287c00-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Ms. Naaja H. Nathanielsen",
            //      "url": "https://oiw.no/speaker/ms-naaja-h-nathanielsen",
            //      "description": "Naaja H. Nathanielsen is Minister of Business, Trade, Mineral Resources, Justice and Gender Equality in Greenland. She was first elected to Inatsisartut in 2009 for Inuit Ataqatigiit. The following 7 years she served on a number of committees, especially the Finance and Fiscal affairs.",
            //      "@type": "EducationGroup",
            //      "@id": "fqonTtodcrOOdoYy4Lqy85",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/72658a4a77f41ccf9d3c6e1c8708b3e880d0c554-652x652.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Áslat Holmberg",
            //      "url": "https://oiw.no/speaker/áslat-holmberg",
            //      "description": "Aslak Holmberg is from Njuorggán, which is situated on the banks of the great river Deatnu, which today forms the border between Finland and Norway. He is a salmon fisher, teacher and holds a master degree in indigenous studies. He is a former member of the Sámi parliament of Finland.",
            //      "@type": "EducationGroup",
            //      "@id": "fqonTtodcrOOdoYy4Lr48t",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/6e66a3971ea69fbbf939835fe2cb610e6cedb048-704x528.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Lola Akinmade Åkerström",
            //      "url": "https://oiw.no/speaker/lola-akinmade-åkerström",
            //      "description": "Lola Akinmade Åkerström is an award-winning visual storyteller, international bestselling author, and travel entrepreneur. She has dispatched from over 80+ countries and her work has been featured in National Geographic, New York Times, The Sunday Times, The Guardian, BBC, CNN, Travel Channel, Travel + Leisure, Lonely Planet, Forbes, and many more.",
            //      "@type": "EducationGroup",
            //      "@id": "o9fWudzD75M43qA95oKvJg",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/fb66d5990f9e1c8b3b1a00af0e2cf58cefa5d834-1800x2400.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Winta Negassi",
            //      "url": "https://oiw.no/speaker/winta-negassi",
            //      "description": "Winta Negassi previously worked at Warner Brothers Discovery as Vice President, People and Culture for the Nordic region. She brings international experience, having studied and worked in London for 13 years. Winta has a Bachelors in International Relations and a Masters in International Human Resource Management from Kingston University.\n\n",
            //      "@type": "EducationGroup",
            //      "@id": "IB3GRND3P7nAlGsNFxyWlE",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/607093a92c2c9316f826d84c7daa08d3a7170fbe-510x510.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Abu Bundu-Kamara",
            //      "url": "https://oiw.no/speaker/abu-bundu-kamara",
            //      "description": "Abu is a globally recognised equity, inclusion and diversity expert and strategic thinker with strong business acumen.  He is passionate about helping leaders and companies embrace change in a New World Marketplace. With a proven record and over 20 years of career experience in global business sectors and senior HR leadership roles. ",
            //      "@type": "EducationGroup",
            //      "@id": "o9fWudzD75M43qA95oLgoC",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/c3df7b7194e6c6f17044e8cad704cbd42d672419-600x600.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Edson Lopes",
            //      "url": "https://oiw.no/speaker/edson-lopes",
            //      "description": "Edson is a proud gay Brazilian living in the captivating landscapes of Norway. His journey through a rich tapestry of diverse cultures and continents has ultimately led him to his current role as a Global Senior Leadership Development Manager and a vital member of the DEI Core Team at Yara International in Norway.\n\n",
            //      "@type": "EducationGroup",
            //      "@id": "o9fWudzD75M43qA95oLoHQ",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/0ef01e1c763f895bcaf699540e24c4ddb0385acc-300x300.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Astrid Sundberg",
            //      "url": "https://oiw.no/speaker/astrid-sundberg",
            //      "description": "Operation Smile is a global nonprofit advancing health equity by providing cleft surgery, comprehensive care and medical training. Strengthening local healthcare systems and workforces, the organization collaborates with volunteers, governments, nonprofits, donors, and corporate partners to contribute to a world where healthcare access is a right, not just a privilege.\n\nWith over 20 years of experience in startups and scale-ups, Astrid is a future-focused leader who has driven significant change in the corporate sector, building and growing business streams while leading organizational transformation. Known for her highly collaborative approach, Astrid\u0027s fervent belief in inclusive leadership has propelled numerous impact-driven initiatives and campaigns. Throughout her career, she has successfully led cross-functional teams, stakeholders, and project groups, showcasing her adaptability and co-creative spirit.\n\nA champion for change, Astrid\u0027s expertise spans STEM, consultancy, retail, hospitality, and sustainability. She previously served as Global Director of Diversity, Inclusion, and Belonging at Norway\u0027s first unicorn company, Oda, where she implemented a high-impact program recognized with three national awards. Prior to that, she worked at Amesto, one of Norway\u0027s most innovative companies. More recently, she was the Director of Impact Partnerships at The Conduit, a collaborative community of changemakers driving transformative social impact.\n\nAstrid’s dedication to humanitarian work aligns seamlessly with her current role at Operation Smile Norway, fulfilling her ambition to contribute to the non-profit sector. In her spare time, she mentors and advocates for the inclusion of women in tech and innovation, particularly those from underserved communities. She also advocates for gender equity and human rights, addressing often-taboo subjects like sexual harassment and gender-based violence to break the silence and dismantle barriers, empowering others to speak out.\nA regular speaker and moderator at conferences across Europe and the UK, she also serves as a board advisor for several NGOs, including Women in Tech Oslo, Diversify Consult, and Humans for Humans, an organization providing mental healthcare to people who have been affected by human trafficking.",
            //      "@type": "EducationGroup",
            //      "@id": "fqonTtodcrOOdoYy4Lsdd3",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/3b4520c39b54e6158e3321c5abda3e2e013c7b18-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Neil Chatterjee",
            //      "url": "https://oiw.no/speaker/neil-chatterjee",
            //      "description": "Neil is the Director of Market Management for Expedia Group, with a team focused on the managing key supplier relations. He is a huge proponent of travel being a force for good and has also worked as a business mentor to startups in the travel space. Expedia Group is one of the world’s most dynamic travel companies. ",
            //      "@type": "EducationGroup",
            //      "@id": "fqonTtodcrOOdoYy4LsnvW",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/b9a955046c1d79ff6155e1c50238436884dbcce4-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Tor Andreas Bremnes",
            //      "url": "https://oiw.no/speaker/tor-andreas-bremnes",
            //      "description": "Tor is an economist, leader, entrepreneur, with a great passion for creating a world where we unlock the potential in each of us. His greatest strength is curiosity and drive, it also happens to be that he has cerebral palsy, some hearing and sight loss, and is engaged to his wonderful fiancee Thomas.",
            //      "@type": "EducationGroup",
            //      "@id": "IB3GRND3P7nAlGsNFxzQr3",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/a9005ec1e622229b2df5084af6d5cf2009f19df2-450x450.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Chi Lee",
            //      "url": "https://oiw.no/speaker/chi-lee",
            //      "description": "Chi Lee has a Masters Degree in Museum Studies from Harvard University.  For over two decades, they have been a freelance consultant for design/build studios working with global retail companies. Chi previously worked as a liaison and organizer for Arts for Humanity, a non-profit bringing the arts to underserved communities.",
            //      "@type": "EducationGroup",
            //      "@id": "o9fWudzD75M43qA95oMfSC",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/016b261360a92c5b0040af1b8498ed265d59e742-450x450.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Thandi Dyani",
            //      "url": "https://oiw.no/speaker/thandi-dyani",
            //      "description": "Thandi Dyani is an independent consultant, and external Network Organizer for Africa Region \u0026 the Nordics in the Emergent Team of the BMW foundation working with Leaders to inspire responsible leadership and catalyze change aligned with the SDGs.",
            //      "@type": "EducationGroup",
            //      "@id": "IB3GRND3P7nAlGsNFxzkx1",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/baac1a7e4744814c764444cbe63e6b60755c62ef-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Safir Boukhalfa",
            //      "url": "https://oiw.no/speaker/safir-boukhalfa",
            //      "description": "Safir is a DEI consultant, writer, and founder living in Berlin, Germany. They also happen to be from Algeria and France, queer, Muslim, and Non-Binary with autism and ADHD. Their purpose is to guide companies and NGOs on how to help people from marginalized communities not only to feel seen, heard, and included but also embraced for who they are.",
            //      "@type": "EducationGroup",
            //      "@id": "IB3GRND3P7nAlGsNFy022h",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/f23430c5dee918887f0108ec63a8da1117e5bb57-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Iva Ogrizovic",
            //      "url": "https://oiw.no/speaker/iva-ogrizovic",
            //      "description": "Iva Ogrizovic is a Director at Diversify and People \u0026 Culture Partner at Diversify Consult. At Diversify Consult, Iva focuses on developing and implementing Diversity, Equity, Inclusion and Belonging (DEIB) strategies and co-designs and delivers DEIB training and development programs.",
            //      "@type": "EducationGroup",
            //      "@id": "fqonTtodcrOOdoYy4Luoda",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/aad102036b85d18ed1142e112636d060a3ef5303-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Mark Ivan Serunjogi",
            //      "url": "https://oiw.no/speaker/mark-ivan-serunjogi",
            //      "description": "Mark Ivan is a specialist in DEI branding and change management, exploring how management processes affect organizational cultures and employee attraction value \u0026 retention power. His point of departure is that an authentic employer brand is built from employee experiences and that an attractive workplace is inclusive at worst and equitable at best",
            //      "@type": "EducationGroup",
            //      "@id": "fqonTtodcrOOdoYy4LvEzB",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/a739b9f03e10b95f464c7231af74c09bf25150f4-450x450.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Melanie Coffee",
            //      "url": "https://oiw.no/speaker/melanie-coffee",
            //      "@type": "EducationGroup",
            //      "@id": "IB3GRND3P7nAlGsNFy1aBz",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/84a107019fe146a639690d8f4b3e4b05da6dc377-433x433.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Dr. Ferdinand Mirbach",
            //      "url": "https://oiw.no/speaker/dr-ferdinand-mirbach",
            //      "description": "Ferdinand is a senior expert on integration and inclusion as well as diversity officer at the Robert Bosch Foundation, Germany. Good coexistence in heterogeneous societies, equal participation of people with and without a migration history, and combating discrimination are his motivation for working for the foundation.\n\n",
            //      "@type": "EducationGroup",
            //      "@id": "fqonTtodcrOOdoYy4Lvy8A",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/5fa62919e0cd00be8a0551f530b61101a3754fee-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Stephen Burrell",
            //      "url": "https://oiw.no/speaker/stephen-burrell",
            //      "description": "Stephen is a highly experience senior executive with a strong passion for people.\nThroughout his corporate career, he has held various leadership positions such as sales\nand marketing director, brand manager, production manager and senior project manager. ",
            //      "@type": "EducationGroup",
            //      "@id": "o9fWudzD75M43qA95oQAjA",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/bed12492c46e5e2843bbdb4dc883076c9591a1fc-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Antony Hutchings",
            //      "url": "https://oiw.no/speaker/antony-hutchings",
            //      "description": "Antony Hutchings holds the position of Director of Organizational Development, Global Regions at NOV. In 2003, Antony has a Bachelor of Arts, majoring in Industrial Relations and Workplace Law at the University of Western Australia. Following graduation, Antony worked in a number of industries before moving to Norway in 2013. ",
            //      "@type": "EducationGroup",
            //      "@id": "o9fWudzD75M43qA95oRbVg",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/02e12e848d37b2b958c4156fe55f4c4fd15bb178-451x451.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Plamena Cherneva",
            //      "url": "https://oiw.no/speaker/plamena-cherneva",
            //      "description": "Plamena Cherneva is an IT professional, passionate entrepreneur and relentless Diversity and Inclusion advocate.\nAfter graduating with MSc in Computer Science and Engineering from Technical University of Denmark \u0026 experiencing all possible challenges as a minority in the Industry, she established WonderCoders NGO, to support and inspire more women.",
            //      "@type": "EducationGroup",
            //      "@id": "o9fWudzD75M43qA95oS4uu",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/91a16565671aa303f271a3d100db592d6856baec-400x400.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Loubna Messaoudi",
            //      "url": "https://oiw.no/speaker/loubna-messaoudi",
            //      "description": "Loubna Messaoudi is the CEO Founder of BIWOC* Rising. She is a former airline pilot, holds a degree in media science and philosophy and also further training in sound and video design. Her career fields cover Cultural Institutions, Film Festivals, Film/TV Productions and NGOs in Germany and New Zealand.",
            //      "@type": "EducationGroup",
            //      "@id": "fqonTtodcrOOdoYy4LzLRw",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/21732833b9fa38e488e2e0cc3a202fd946754597-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Mette Buhl Christoffersen",
            //      "url": "https://oiw.no/speaker/mette-buhl-christoffersen",
            //      "@type": "EducationGroup",
            //      "@id": "IB3GRND3P7nAlGsNFy4oPh",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/223f468afeeb1d0f43772c83243f5cda69800ceb-533x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Sarah Reynolds",
            //      "url": "https://oiw.no/speaker/sarah-reynolds",
            //      "description": "Sarah Reynolds is a creative, award-winning marketing executive and advocate for diversity, equity, inclusion, and belonging (DEIB). A veteran of the HR software market, Sarah is currently Chief Marketing Officer at Hibob, where they bring their deep industry knowledge, passion for storytelling.",
            //      "@type": "EducationGroup",
            //      "@id": "o9fWudzD75M43qA95oSrma",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/7d0b75fe6d16ef68c1a9e71e07c70b96fb380b9e-450x450.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Charlotte Jónsdóttir Biering",
            //      "url": "https://oiw.no/speaker/charlotte-jónsdóttir-biering",
            //      "description": "Charlotte Biering is Marel’s first D\u0026I Specialist, based in Iceland but operating across 30+ countries. She brings a multidisciplinary \u0026 intersectional approach to her work, drawing from her experiences working across multiple industries and cultures.  She spent years working in international development, disaster response and gender equality.",
            //      "@type": "EducationGroup",
            //      "@id": "o9fWudzD75M43qA95oT84u",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/b3e679cd0b92878f4ef9935bddecddd0faca4894-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Thorey V. Proppe",
            //      "url": "https://oiw.no/speaker/thorey-v-proppe",
            //      "description": "Thorey V. Proppe is the CEO and founder of Alda, an Icelandic tech company which developed a DEI software for workplaces globally. Alda is the next generation of DEI software with all in one: The Inclusion Index and Pulse, gamified micro learning content, assessments, goals \u0026 KPI´s – focusing on a sustainable DEI journey. ",
            //      "@type": "EducationGroup",
            //      "@id": "fwluKSLh9gfPaX1JBnbln2",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/aa83eccc870cfadcf185fe8a5d3bda7db3bb49d5-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Kogulan Kugathasan",
            //      "url": "https://oiw.no/speaker/kogulan-kugathasan",
            //      "description": "Kogulan has nearly a decade of experience in Diversity, Equity \u0026 Inclusion and Programme Management. Ambitiously, he has held various international roles in big corporations – working remotely and in office. Passionately, his profile is centered around change management and diversity \u0026 inclusion in international context.\n\n",
            //      "@type": "EducationGroup",
            //      "@id": "ia7ghcj6Cj8sG7Ap3w3KvH",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/0a5aa45fb5d14db140756442e55e82826b5d1b05-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Lawrence Akpore",
            //      "url": "https://oiw.no/speaker/lawrence-akpore",
            //      "description": "Lawrence Akpore is a Nigerian American citizen. His background is in Chemical, Manufacturing, and Systems Engineering. He holds multiple academic degrees including a Bsc., MSc. and an MBA. He is currently pursuing a master’s degree in Organizational Management at Harvard University.",
            //      "@type": "EducationGroup",
            //      "@id": "q2pseckBWkABOLusJcSKGy",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/8cad1e764010b321ceccfa28372eb99a21e5e28e-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Michael Watson",
            //      "url": "https://oiw.no/speaker/michael-watson",
            //      "description": "Michael’s original background is in banking, finance \u0026 trading. For 20+ years has been a senior leader in Nordea’s Capital Markets Division, leading teams across Nordic countries and international capitals. Inspired by Gestalt psychology he later changed career priorities \u0026 the latest 15 years he has been People Business Partner in Nordea.",
            //      "@type": "EducationGroup",
            //      "@id": "ia7ghcj6Cj8sG7Ap3w4Omf",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/0ac2d725e2eee0f42d89b6c234b4712d44d7a2cf-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Henri Terho",
            //      "url": "https://oiw.no/speaker/henri-terho",
            //      "description": "Henri Terho is the Head of Arts Support, in which he is responsible for the funding schemes and developmental projects. Taike is a governmental agency to promote artists \u0026 arts organizations with grants, subsidies and various projects. Terho has also worked as the chair of Finnish State Art Commission \u0026 promote artists and arts organizations.",
            //      "@type": "EducationGroup",
            //      "@id": "ia7ghcj6Cj8sG7Ap3w4zmw",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/43baf275017bc4aa90a4f2de344591b35e9fe309-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Tania Nathan",
            //      "url": "https://oiw.no/speaker/tania-nathan",
            //      "description": "Tania Nathan is a writer, producer, community organizer and artist based in Helsinki. Issues that are close to her heart include decolonialism, anti capitalism, joy, rest and community. In her free time, she is endlessly preoccupied with her rescue dog Lilli, exploring nature and cooking for her loved ones.\n\n",
            //      "@type": "EducationGroup",
            //      "@id": "q2pseckBWkABOLusJcUoT2",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/e6d407bf85691ceb51aecacf86763e0b69a455cb-451x451.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Justin Hester",
            //      "url": "https://oiw.no/speaker/-justin-hester",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "q2pseckBWkABOLusJcV7RV",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/8fc14f58c5015ac1d6cddadb369a781083ac1be0-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Aminkeng A Alemanji",
            //      "url": "https://oiw.no/speaker/aminkeng-a-alemanji",
            //      "description": "Aminkeng A Alemanji (Amin) researches race, racism, antiracism and antiracism education in Finland. His research focuses on developing different strategies \u0026 methods of antiracism education in and out of schools. In January (2022) he released Finland’s first Antiracism mobile App – Finland without Racism, available for download on the Google play.",
            //      "@type": "EducationGroup",
            //      "@id": "fwluKSLh9gfPaX1JBnfvtW",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/e78e68ed3e6d78e3d050c2962d9800aa99bfea9d-450x450.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Lynn Roseberry, Ph.D",
            //      "url": "https://oiw.no/speaker/lynn-roseberry,-ph-d",
            //      "description": "Lynn Roseberry, Ph.D., is the owner and director of On the Agenda, an equity, diversity and inclusion consultancy based in Copenhagen. She has more than 25 years of experience working with EDI issues as a lawyer, scholar, manager, \u0026 consultant. Before launching On the Agenda in 2017, Lynn was associate professor at Copenhagen Business School.",
            //      "@type": "EducationGroup",
            //      "@id": "q2pseckBWkABOLusJcVsa8",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/fd1f2c8e6b639b91eea1573808bed8b9c37fd3af-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Marcus Jarl",
            //      "url": "https://oiw.no/speaker/marcus-jarl",
            //      "description": "Marcus Jarl is a behavioral science practitioner and ACT-trained therapist with nearly 30 years of experience with organizational and leadership development. He is driven by curiosity, skepticism, learning, experimentation, teaming, inclusion and extremely interested in how we as human beings spend our time and energy.\n\n",
            //      "@type": "EducationGroup",
            //      "@id": "q2pseckBWkABOLusJcWjhZ",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/a71e91b3e0cf862d00d7c252c727d1bcd3f203e5-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Adamu Haruna",
            //      "url": "https://oiw.no/speaker/adamu-haruna",
            //      "description": "Adamu Haruna is a Senior Solutions Architect at Amazon Web Services \u0026 Leader of BEN(Black Employee Network) for Amazon\u0026AWS in the Nordics and a part of the Glamazon leadership team at Amazon\u0026AWS in the Nordics. Adamu has a background of working as both an individual technical contributor as well as a ID\u0026E  leader in different IT companies.",
            //      "@type": "EducationGroup",
            //      "@id": "fwluKSLh9gfPaX1JBniCbK",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/2f386b87937d7f83761c65c976d878e46b1d7c74-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Anna Conneryd Lundgren",
            //      "url": "https://oiw.no/speaker/anna-conneryd-lundgren",
            //      "description": "Anna saw no obstacles on her way to the top. With a combination of brain and heart, she stepped into the role of Chief People Officer at the large cap company Elekta. Elekta brings together employees to help healthcare \u0026 the world cure cancer with laser radiation. Anna loves the concept of psychological safety as a complement to tools she learned.",
            //      "@type": "EducationGroup",
            //      "@id": "ia7ghcj6Cj8sG7Ap3w7XeR",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/9161527023aeec0c308972e49739e1016805d702-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Daniel Brämhagen",
            //      "url": "https://oiw.no/speaker/daniel-brämhagen",
            //      "description": "Daniel is close to 30 years of experience whin consulting focusing on driving change in transformations. With consistent focus on the human aspects of change, he is a strong advocate for purpose driven initiatives using innovation and behavior science to find new ways. The last 6 years he has been focusing on people sustainability.",
            //      "@type": "EducationGroup",
            //      "@id": "ia7ghcj6Cj8sG7Ap3w81Pg",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/071ac59411e662476901d9d635c0fe6b0c11ee7e-500x500.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Flemming Kehr",
            //      "url": "https://oiw.no/speaker/flemming-kehr",
            //      "description": "Flemming is an experienced top management and board advisor with more than 20 years of career experience in global business \u0026 a proven track record in assisting listed and non-listed clients in APAC, EMEA, and US. He is passionate about strategy, leadership, and people, he has designed \u0026 led multiple client tailored development \u0026 change programmes.",
            //      "@type": "EducationGroup",
            //      "@id": "ia7ghcj6Cj8sG7Ap3w8Xab",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/6bb8c28f0dfa511a7de614b0c64a2a4c37577f78-416x416.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Ritika Wadhwa",
            //      "url": "https://oiw.no/speaker/ritika-wadhwa",
            //      "description": "Ritika is the Founder and CEO of Prabhaav Global. Ritika was born and brought up in India \u0026 went to achieve an MBA in the UK \u0026 broke all barriers as an immigrant to Canada \u0026 the UK. Ritika has over 30 years of experience working at Board level across 3 continents, with senior stakeholders within the public and private sector. ",
            //      "@type": "EducationGroup",
            //      "@id": "ia7ghcj6Cj8sG7Ap3w9Kvd",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/5270a0ec799220e79ff34d557915aa9c4cd7349c-2571x3600.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Ingibjörg Lilja Þórmundsdóttir",
            //      "url": "https://oiw.no/speaker/ingibjörg-lilja-þórmundsdóttir",
            //      "description": "Ingibjörg Lilja’s passion is to empower people, teams and organizations to become stronger, faster and more successful in a sustainable way. Ingibjörg Lilja or Ingsa has broad experience working with leadership within tech, both in Iceland and for international companies. Ingsa is the CHRO of Helix Health the leading health tech company in Iceland.",
            //      "@type": "EducationGroup",
            //      "@id": "ia7ghcj6Cj8sG7Ap3wA1KL",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/2f167359b5ef42bde929941cc2997e5b484b46cc-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Amy Baker",
            //      "url": "https://oiw.no/speaker/amy-baker",
            //      "description": "Amy Baker is Ambassador of Canada to Norway. She comes to this role after four years as Canada’s Deputy Ambassador to France, serving also as Head of Mission for over a year during this time period. Previously, Ms. Baker was Director General of Health and Nutrition at Global Affairs Canada. In this role, she represented Canada on Multilateral Board",
            //      "@type": "EducationGroup",
            //      "@id": "fwluKSLh9gfPaX1JBnxK0G",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/d52adb42412c5cafaa92481a29e929fa9d9744bd-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Greg Fernando",
            //      "url": "https://oiw.no/speaker/greg-fernando",
            //      "description": "Greg Fernando is the founder of the NGO New to Sweden, which works to help internationals integrate into the Swedish labour market and within the Swedish community. A vocal proponent of newcomer rights, Greg has delivered talks to employers like Google, SEB, Vattenfall and Spotify. He recently co-founded the Swedish 1046 Inclusion Campaign.",
            //      "@type": "EducationGroup",
            //      "@id": "q2pseckBWkABOLusJcjSBL",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/91cd0e0d93c528ffb264ceb75c3abb2e013c73b3-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Lars Rinnan",
            //      "url": "https://oiw.no/speaker/lars-rinnan",
            //      "description": "Lars Rinnan is a visionary CEO, angel investor, board member, public speaker, and futurist from Oslo, Norway. Lars has more than 25 years of management experience and has started 6 companies within business analytics and artificial intelligence, all joined together in Amesto NextBridge, he is the former CEO of Amesto NextBridge.",
            //      "@type": "EducationGroup",
            //      "@id": "q2pseckBWkABOLusJcjeJ4",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/c7b3ba81d9843d7b1bbfe313398be6dca6d3507c-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Sarah M Przedpelska",
            //      "url": "https://oiw.no/speaker/sarah-m-przedpelska",
            //      "description": "Sarah M Przedpelska is an educator and former researcher, who likes feelings, people and justice, and has learnt as much, if not more, outside of academia, as within it. Through her research in Canada and Norway she’s worked and published on migrant belonging, transferable skills and mentoring.\n\n",
            //      "@type": "EducationGroup",
            //      "@id": "fwluKSLh9gfPaX1JBnxwmO",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/8983278afb9fc878acf8a20de2c050ad8cea9349-250x250.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Tendai Angela Jambga-Rokkones",
            //      "url": "https://oiw.no/speaker/tendai-angela-jambga-rokkones",
            //      "description": "Tendai Angela Jambga-Rokkones is a yoga teacher trained in vinyasa yoga, restorative yoga, and yoga Nidra; wellness curator, international speaker, and avid reader. Tendai curates programs and talks derived from the fields of Wellbeing, Yoga, African philosophy, Ayurveda, and Self-care as a form of innovative transformation for one’s Self.",
            //      "@type": "EducationGroup",
            //      "@id": "ia7ghcj6Cj8sG7Ap3wF4UJ",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/2d752ce38090663384272bff56e96d1f83719be2-250x250.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Selena Støback",
            //      "url": "https://oiw.no/speaker/selena-støback",
            //      "@type": "EducationGroup",
            //      "@id": "ia7ghcj6Cj8sG7Ap3wFDCY",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/d8057a5e51d740c2d27bc3d9060ea873bdef42ad-4570x4570.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Kerin Ayyalaraju",
            //      "url": "https://oiw.no/speaker/kerin-ayyalaraju",
            //      "description": "Kerin Ayyalaraju has been Australian Ambassador to Denmark, Norway and Iceland since August 2021. She is a senior officer with the Department of Foreign Affairs and Trade whose extensive career has seen her deployed in many countries, frequently in crisis situations, and across a range of functions at headquarters in Canberra since 1994.",
            //      "@type": "EducationGroup",
            //      "@id": "LxW2c7ek5iDOrc2j9UuDeV",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/96047c765e1c9bd5392e9e291b046dd6cbbc3dec-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Stine Rebekka Aksnes",
            //      "url": "https://oiw.no/speaker/stine-rebekka-aksnes",
            //      "description": "As the former Director of Sustainability at Sopra Steria, Stine had the responsibility for the development, implementation and reporting of the company’s overall sustainability strategy. She holds several board positions including Oslo Venstre, and Oslo Inklusive, whose mission is to foster inclusive cultural spaces to drive social impact.",
            //      "@type": "EducationGroup",
            //      "@id": "jjBq4X6gdc6IuElj7uJYkt",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/915eadb6b57f922709476e28d1eb2747b22f184a-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Salamatu Kamara",
            //      "url": "https://oiw.no/speaker/salamatu-kamara",
            //      "description": "In 2018, I independently published my first book \"What do you Wear Under your Clothes?\". For many years I have been a champion and advocate in the disability rights movement, as well as a board member of the Norwegian Association of Disabled (NAD).\nSince 2021 I\u0027ve been a news anchor on Supernytt on NRK Super TV channel.",
            //      "@type": "EducationGroup",
            //      "@id": "v3431n7IAaIx7DOZmayz4z",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/dabf4693d759d5ce5c90e87bfd8ea63e0e162b3a-450x450.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Tom Miskin",
            //      "url": "https://oiw.no/speaker/tom-miskin",
            //      "description": "Tom Miskin is the lead of Oslo Innovation Week and a key team member at Oslo Business Region since 2020. With a background in diplomacy, Tom previously worked for the British Foreign Office. A British national, he has resided in Norway for 14 years, bringing a wealth of international experience and a deep understanding of innovation ecosystems. ",
            //      "@type": "EducationGroup",
            //      "@id": "jjBq4X6gdc6IuElj7uK8zY",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/3b9bf9b7225f4dc3dcae58e4e5b2ae82f29c7a4e-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Monica Ifejilika",
            //      "url": "https://oiw.no/speaker/monica-ifejilika",
            //      "description": "Monica Ifejilika is a Norwegian-Nigerian singer, songwriter, and composer. The Black Arts collective has made its mark on Norwegian Arts and Culture for two decades with an Afropean musicality and a distinct political voice on both albums and onstage. Since 2021, Monica has also worked as a senior advisor at the Arts \u0026 Culture Council in Norway.",
            //      "@type": "EducationGroup",
            //      "@id": "v3431n7IAaIx7DOZmazNIb",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/173387f16951cd921cfe84cec32e583ecbda0527-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Martin Devor",
            //      "url": "https://oiw.no/speaker/martin-devor",
            //      "description": "Martin Devor is head of Diversity, Equality, and Inclusion at Aker Solutions globally. He holds a master’s degree in organizational psychology. Prior to his current role, he spent several years at Equinor, working across roles in HR, finance, M\u0026A and business development of renewable energy projects. ",
            //      "@type": "EducationGroup",
            //      "@id": "v3431n7IAaIx7DOZmazn4B",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/666c06c1687a1fbd1090cf9a7ec5cba20acc547d-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Ida Helene Benonisen",
            //      "url": "https://oiw.no/speaker/ida-helene-benonisen",
            //      "description": "Ida Helene Benonisen (she/they) is a Norwegian-Sámi spoken word poet and activist, with a degree in journalism. She uses her poetry both as a way to describe personal subjects, such as identity and love, but also as a tool for her activism and political involvement. Ida was also one of the people who occupied the Norwegian Ministry of Oil and Energ",
            //      "@type": "EducationGroup",
            //      "@id": "jjBq4X6gdc6IuElj7uKkPN",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/d1b6b1b87cac3f29678bc1feec5e6ecbd24aa7ef-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Anne Lajla Utsi",
            //      "url": "https://oiw.no/speaker/anne-lajla-utsi",
            //      "description": "Anne Lajla Utsi belongs to the Sámi people, and is based in the village of Guovdageaidnu, which lies above the Arctic Circle in Norway. Since 2009, Anne Lajla has been the driving force behind the International Sami Film Institute (ISFI). As CEO of ISFI, she built a robust international film network, collaborating with Sundance Film Institute.",
            //      "@type": "EducationGroup",
            //      "@id": "v3431n7IAaIx7DOZmb0Ofd",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/7dd092f00feac011f030b83e72541af10b11aab4-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Siw Andersen",
            //      "url": "https://oiw.no/speaker/siw-andersen",
            //      "description": "Siw Andersen is the CEO of Oslo Business Region and a board member of Galleri ROM. Oslo Business Region, owned and funded by the City of Oslo, aims to attract international investments and talent, bolstering the city’s startup ecosystem and high-growth sectors. ROM is a gallery dedicated to the intersection of architecture, art, and urban planning.",
            //      "@type": "EducationGroup",
            //      "@id": "LxW2c7ek5iDOrc2j9UxJew",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/388a8c219e842ae781372790abbb615079b8cb5d-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Nivi Katrine Christensen",
            //      "url": "https://oiw.no/speaker/nivi-katrine-christensen",
            //      "description": "Since 2015, Nivi Christensen has been the museum director at Nuuk Art Museum in Kalaallit Nunaat / Greenland. She holds a Master’s degree in Art History from the University of Copenhagen, where she specialized in art from and about Greenland. She is a regular writer and commentator on Art from Greenland both locally and internationally.",
            //      "@type": "EducationGroup",
            //      "@id": "v3431n7IAaIx7DOZmb0hKt",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/f46937a40c976efbe7d47343d56489d68a68cdba-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Phaedria Marie St.Hilaire",
            //      "url": "https://oiw.no/speaker/phaedria-marie-st-hilaire",
            //      "description": "A Dominican-born, former science academic researcher and corporate leader with 20+ years experience in pharma and biotech. She has a proven track record of inspiring and driving cross-functional, multicultural teams to deliver results from her leadership positions at global companies like Carlsberg and Novo Nordisk.",
            //      "@type": "EducationGroup",
            //      "@id": "jjBq4X6gdc6IuElj7uMQwF",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/42f834eac62868679a6ff51c21c2a3bab013e4d0-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Arbab Dar",
            //      "url": "https://oiw.no/speaker/arbab-dar",
            //      "description": "Arbab Dar serves as the Country Managing Partner/CEO for EY in Norway, a leading multinational professional services firm specializing in assurance, tax, law, consulting, and strategy services. He advises clients within the banking and insurance industries, as well as asset managers, in addition to leading EY’s operations in Norway.",
            //      "@type": "EducationGroup",
            //      "@id": "v3431n7IAaIx7DOZmb2AqH",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/1c2c7bc015e3179c46fdff4948c5fc6810148944-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Faye Hobson",
            //      "url": "https://oiw.no/speaker/faye-hobson",
            //      "description": "Faye is Director of Culture at Salzburg Global Seminar, where she leads the Culture Program Pillar. Her focus revolves around catalyzing cultural and leadership transformations to shape a more equitable and sustainable world. She designs, develops, and executes initiatives like the Arts and Society program series among others.",
            //      "@type": "EducationGroup",
            //      "@id": "v3431n7IAaIx7DOZmb2OMa",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/1b503e8de5290937bf690e2cddfea2f487f25c69-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Katja Vahl",
            //      "url": "https://oiw.no/speaker/katja-vahl",
            //      "@type": "EducationGroup",
            //      "@id": "jjBq4X6gdc6IuElj7uNwSH",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/591488b77e0812e421bfb2f5dda5590294d35f09-2048x1365.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Fadzi Whande",
            //      "url": "https://oiw.no/speaker/fadzi-whande",
            //      "description": "Fadzi Whande is an award-winning Global Diversity and Inclusion Strategist and Social Justice Advocate dedicated to driving transformational cultural change on a global scale. With a career spanning over two decades, she has successfully designed and implemented large-scale DEI initiatives that drive significant global change. ",
            //      "@type": "EducationGroup",
            //      "@id": "v3431n7IAaIx7DOZmb3DGE",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/d5b2982ada0c44e284cb5a96eefb55163147b29c-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "John Michael Schert",
            //      "url": "https://oiw.no/speaker/john-michael-schert",
            //      "description": "John Michael Schert is an artist, advisor and producer working in multiple sectors. Schert is the founding principal of JMS \u0026 Company, an executive producer of Boise’s B Corp Treefort Music Fest, and the founder of the American Ballet Theatre Leadership Lab. ",
            //      "@type": "EducationGroup",
            //      "@id": "v3431n7IAaIx7DOZmb3ReA",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/567d4562a21e3835e4d492bbf382d889fe26f850-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Alex Jørgensen",
            //      "url": "https://oiw.no/speaker/alex-jørgensen",
            //      "description": "Alex is a seasoned journalist with over 12 years of experience covering Middle Eastern politics, wars, conflicts, and terrorism. Having collaborated with renowned organizations like UNDP and UNHCR, Alex’s work primarily focuses on critical issues such as integration, global peace, and refugee rights. ",
            //      "@type": "EducationGroup",
            //      "@id": "jjBq4X6gdc6IuElj7uOVPg",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ecfcc295ad91f0b6fa8bae252fa2ffc260514594-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Garima Singh",
            //      "url": "https://oiw.no/speaker/garima-singh",
            //      "description": "At Ikea, Garima leads global teams of architecture (data \u0026 tech), engineering services and foundational platforms (DataOPs, MLOPs, Integrations etc). A leader with extensive experience in tech (Data/AI, Cloud, IOT/connectivity), and international speaker. She is co-author of several ISO standards and recipient of several data and leadership awards.",
            //      "@type": "EducationGroup",
            //      "@id": "jjBq4X6gdc6IuElj7uPeDM",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/558b8e48f1d0514bff549d283d975fb9e774877d-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Áslaug Eva Björnsdóttir",
            //      "url": "https://oiw.no/speaker/áslaug-eva-björnsdóttir",
            //      "description": "Driven by curiosity and a passion for innovation, Aslaug focuses on team development and digital transformation in her work as a Delivery Lead at Gangverk, a leading software development company specializing in driving digital transformation businesses across various industries. ",
            //      "@type": "EducationGroup",
            //      "@id": "jjBq4X6gdc6IuElj7uPmrs",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/d12ed0419ba7ea9c00aa6d48c40543bb1728ae4f-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Jeff Kwasi Klein",
            //      "url": "https://oiw.no/speaker/jeff-kwasi-klein",
            //      "description": "Jeff Kwasi Klein is a dedicated advocate, strategist, and thinker for social justice, equity, and political innovation. He is the Co-Founder and Co-CEO of the Imagineers Lab, an interdisciplinary space for holistic transformation and co-creating desirable futures. ",
            //      "@type": "EducationGroup",
            //      "@id": "v3431n7IAaIx7DOZmb4tKe",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/dcebe4815ee740a5fedb12672eee33a5394733e6-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Regine Larsen Lindtvedt",
            //      "url": "https://oiw.no/speaker/regine-larsen-lindtvedt",
            //      "description": "Regine Larsen Lindtvedt holds a dual role within Nordic’s largest P\u0026C insurer, If, where she is the Nordic Head of Market Strategy and the CEO of If’s sister company, If Services. In her role, her focus is to drive initiatives related to growth and innovations within for instance property, health and sustainability through strategic partnerships.",
            //      "@type": "EducationGroup",
            //      "@id": "LxW2c7ek5iDOrc2j9V3G6F",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/bef4439a25455b324610b16167d52c7cc37d78c0-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Karolina Sveiby",
            //      "url": "https://oiw.no/speaker/karolina-sveiby",
            //      "description": "Karolina is an experienced pedagogue and team leader with solid expertise in training and communication. Currently Karolina leads an international team of teachers at the Berlitz Language and Culture School where she also organizes sought-after intercultural trainings for business.",
            //      "@type": "EducationGroup",
            //      "@id": "jjBq4X6gdc6IuElj7uQLjB",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/923bf4223b6a79049853702c92dec1fb54665d27-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Julie Forchhammar",
            //      "url": "https://oiw.no/speaker/julie-forchhammar",
            //      "description": "Julie Forchhammer is the co-founder of Klimakultur, a Norwegian non-profit founded in 2021 in the mountains in Vang in Valdres. Klimakultur mobilizes the culture sector on how to take action on climate change and implement climate justice.",
            //      "@type": "EducationGroup",
            //      "@id": "v3431n7IAaIx7DOZmb5KVj",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/0e611dc94c426f46b4f49dd18cb511d20fb436a1-451x451.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Alyx Gilham",
            //      "url": "https://oiw.no/speaker/alyx-gilham",
            //      "description": "Alyx is a People and Culture Partner at Hibob’s UK site, where she collaborates with teams across the EMEA locations. In her role, Alyx drives internal culture development and anti harassment trainings to ensure that Hibob\u0027s values are reflected in every aspect of the employee experience. She supports bobbers by fostering a positive and inclusive w",
            //      "@type": "EducationGroup",
            //      "@id": "i25yJA0neSgT1BpxaIQSkM",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/9f71ce1918c2242be7cd7c686ca94027bc1edc68-860x860.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Alex Coburn Davis",
            //      "url": "https://oiw.no/speaker/alex-coburn-davis",
            //      "description": "Alex is a CX Team Leader at HiBob,and dedicated to transforming customer service into a seamless extension of clients\u0027 teams. Based in Lisbon but originally from London, Alex strives for Global support excellence, ensuring that every client interaction is impactful and meaningful. In addition to leading the customer experience team, Alex heads HiBo",
            //      "@type": "EducationGroup",
            //      "@id": "Q01A6nB0EzzzgHsi76Imwq",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/f11d7746d97c49389d16e58d73cedc7e6c224b52-3024x4032.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "diversify-nordics-summit24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Impact",
            //      "Talent",
            //      "Conference"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Diversify Nordics Summit Sending event 'Diversify Nordics Summit' to importer
            //{
            //  "name": "Experience the Power of Behavioral Design: Hands-on Workshop",
            //  "url": "https://oiw.no/event/osman-advisory-services24",
            //  "startDate": "2024-09-25T07:00:00.000Z",
            //  "localStartDate": "2024-09-25T09:00",
            //  "endDate": "2024-09-25T08:30:00.000Z",
            //  "description": "🧠Did you know that understanding human behavior and how to change it is fundamental to any type of transformation?\n\n💡After all, people\u0027s behavior is what drives effective and sustainable change!\n\nWelcome to this workshop on Behavioral Design! \n\n🔍We will share our insights on how companies and governments  can implement behavior change principles in their day-to-day work!\n\n🎯Power-up your toolbox and learn how to apply behavioral science for the real-world! \n\n🌍Learn how to tweak products and services to consumers based on where they live!\n\nWe will also be sharing FREE resources and additional tools you can use in the future.\n\nYour hosts are Sarah Osman and Anniken Juul-Hasund from Osman Advisory Services!\n\n⭐When? 25 September at 9:00 to 10:30\n⭐Where? Sentralen, the 5th floor (Øvre Slottsgate 3, 0157 Oslo)\n\nSee you there!",
            //  "location": {
            //    "name": "SoCentral",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "@type": "Place",
            //    "@id": "PUn43gDAws84BhCBDaAUBD",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Osman Advisory Services",
            //      "url": "https://www.osmanadvisoryservices.com/",
            //      "@id": "4LmnWJ6svv3AwRfuxAgp9T",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/16518668d49d8b743a2e8d6799d381c9088775d7-1419x928.png",
            //      "sameAs": [
            //        "https://www.osmanadvisoryservices.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.osmanadvisoryservices.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Sarah Osman",
            //      "url": "https://oiw.no/speaker/sarah-osman",
            //      "description": "Sarah Osman, with 20 years\u0027 experience in international development and behavioral science, designs effective, evidence-based solutions for nonprofits. Trained in cognitive psychology and global development, she tackles complex challenges across health, migration and education. Sarah is a thought leader in social and behavior change.",
            //      "@type": "EducationGroup",
            //      "@id": "pGegU7xtOJjMtC6RbsMlei",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/e1bb9c3c4b366ac3d562f7d1a329b254240e3c49-457x572.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Anniken Juul-Hasund",
            //      "url": "https://oiw.no/speaker/anniken-juul-hasund",
            //      "description": "Anniken merges insights from behavioral science and consumer behavior, drawing on her expertise in behavioral analysis and international marketing. She has explored strategies to steer consumers towards more sustainable choices, using this knowledge to assist organizations in enhancing their practices and creating a positive impact.",
            //      "@type": "EducationGroup",
            //      "@id": "nNlMDwdaFpeRBQSKRWLx1K",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/93b404f6deed1f63b5d51077ae223c016e813f31-540x540.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "osman-advisory-services24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Impact",
            //      "Scaling",
            //      "Workshop"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Experience the Power of Behavioral Design: Hands-on Workshop Sending event 'Experience the Power of Behavioral Design: Hands-on Workshop' to importer
            //{
            //  "name": "PROFIT HAS NO GENDER- Invest in Women, Accelerate Growth",
            //  "url": "https://oiw.no/event/invest-in-women24",
            //  "startDate": "2024-09-24T08:00:00.000Z",
            //  "localStartDate": "2024-09-24T10:00",
            //  "endDate": "2024-09-24T13:30:00.000Z",
            //  "description": "https://www.facebook.com/coralswans\nhttps://www.linkedin.com/company/coral-swans/\n",
            //  "location": {
            //    "name": "Melahuset",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": " Mariboes gate 8",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "@type": "Place",
            //    "@id": "LxW2c7ek5iDOrc2j9Ruj7H",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Coral Swans International ",
            //      "url": "https://coralswans.org/",
            //      "@id": "i25yJA0neSgT1BpxaIqcoZ",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/dfe9af1fbee8017511e2a107c8f46afc0b8d035c-623x622.png",
            //      "sameAs": [
            //        "https://coralswans.org/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://coralswans.org/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Mrida Greens \u0026 Development",
            //      "url": "https://www.mridagroup.com/",
            //      "@id": "i25yJA0neSgT1BpxaIqnC4",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/7fc5870f05831bb65269724a411ac77a2626d43b-778x712.png",
            //      "sameAs": [
            //        "https://www.mridagroup.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.mridagroup.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Dotline Webmedia",
            //      "url": "https://dotline.no/",
            //      "@id": "NO7Mjjgfso2MMqbm3g6WqZ",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/c475ff0c7a04c73aa5e3d84b057f054cff79f255-940x559.png",
            //      "sameAs": [
            //        "https://dotline.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://dotline.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Richa Coaching",
            //      "url": "https://richa-chandra.com/",
            //      "@id": "i25yJA0neSgT1BpxaIqyVE",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/67ef59745444fb2e66daeba3bc558a59ce8b09f9-722x522.png",
            //      "sameAs": [
            //        "https://richa-chandra.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://richa-chandra.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "iAim College Guidance",
            //      "url": "https://iaim4college.com",
            //      "@id": "NO7Mjjgfso2MMqbm3g6ejK",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/56d8b73c6884abb1ed07c9f561014362fbf3ff47-846x518.png",
            //      "sameAs": [
            //        "https://iaim4college.com"
            //      ],
            //      "gogo": {
            //        "webpage": "https://iaim4college.com"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Opulence Enterprises LLC",
            //      "url": "https://opulence-enterprises-llc.square.site/",
            //      "@id": "i25yJA0neSgT1BpxaIr5X9",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/e261ff080b46889eb2d182d28b946b7f5a13cd4e-763x539.png",
            //      "sameAs": [
            //        "https://opulence-enterprises-llc.square.site/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://opulence-enterprises-llc.square.site/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Arun Nagpal",
            //      "url": "https://oiw.no/speaker/arun-nagpal",
            //      "description": "Arun is a passionate social impact leader with over 40 years of COO, CEO, and board-level experience. He co-founded Mrida, an Indian social enterprise focusing on sustainable and scalable rural development. Under his leadership, Mrida has advanced energy access, education, health, and women’s empowerment, impacting over 38,000 lives.",
            //      "@type": "EducationGroup",
            //      "@id": "jjBq4X6gdc6IuElj7rJiRT",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/91a7074f9f46f13a4a564c89a748388ff10c3df3-225x225.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Anette Miwa Dimmen",
            //      "url": "https://oiw.no/speaker/anette-miwa-dimmen",
            //      "description": "Anette is an experienced brand developer, TEDx speaker, and founder of AWAN (As We Are Now)- a sustainable fashion brand that merges circular principles with tech-driven solutions. It is the only Norwegian fashion brand in the Antler VC portfolio and is currently raising its pre-seed round. She also founded Openbox, a digital giftbook solution.",
            //      "@type": "EducationGroup",
            //      "@id": "jjBq4X6gdc6IuElj7rKUs8",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/b69aaad558f3ad48877dabe301b2cad2ef30329e-183x275.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Sagar Chandna",
            //      "url": "https://oiw.no/speaker/sagar-chandna",
            //      "description": "Sagar is a Sr. partner \u0026 CTO at RunwayFBU and chairman of Tech Nordic Advocates, with 20+ years in tech and VC. He merges tech, data, and people to tackle global challenges. While diversity, impact, and profitability are at the core of his investment approach, Sagar also champions gender equality in tech and inclusive growth through innovation.",
            //      "@type": "EducationGroup",
            //      "@id": "LxW2c7ek5iDOrc2j9RtRJw",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/754e1edf70b543b7ae517db56bfb2da48243c023-225x225.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Kristin Langnes",
            //      "url": "https://oiw.no/speaker/kristin-langnes",
            //      "description": "Kristin Langnes is a sustainable entrepreneurship expert and SEAM methodology specialist, known for inspiring change and connecting ideas. She co-founded SISU Business to aid entrepreneurs with concept development and guidance. Kristin previously co-founded 50 to 100 AS, led Forandring fryder, and worked in IT project management at Canon Norge.",
            //      "@type": "EducationGroup",
            //      "@id": "v3431n7IAaIx7DOZmYIN1g",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/a6a9728970e1e0c9a02c63b18aa2bbafd9975543-200x200.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "LeTonya Esq. Moore",
            //      "url": "https://oiw.no/speaker/letonya-esq-moore",
            //      "description": "LeTonya Moore is a renowned solicitor, entrepreneur, and author with a rich background in representing major organizations, including the U.S. federal government. As a Legalproof Maven™, she aids emerging brands with her 360° Legalproof Brand Protection Methodology™, a strategic blueprint for global business growth. ",
            //      "@type": "EducationGroup",
            //      "@id": "jjBq4X6gdc6IuElj7rMSl5",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/8dea49fe5caa7ed3eb53f8238251b0ea78597a0a-1170x1665.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Linnéa Engström",
            //      "url": "https://oiw.no/speaker/linnéa-engström",
            //      "description": "Linnéa Engström, a Swedish environmentalist and politician, served as a Member of the European Parliament, gender equality coordinator for the Green Party, and worked on development aid. Focused on sustainability, gender equality, and fisheries, she authored the EU’s first political report as well as two books on climate justice and feminism.",
            //      "@type": "EducationGroup",
            //      "@id": "v3431n7IAaIx7DOZmYId0P",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/0e7048e92404134a27dd4105563a89b5336b4781-219x231.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Anupama Tadanki",
            //      "url": "https://oiw.no/speaker/anupama-tadanki",
            //      "description": "Anupama Tadanki brings together expertise in strategic planning and philanthropy, currently working as a strategy consultant at McKinsey \u0026 Company. She previously worked with the Hewlett Foundation\u0027s Global Development \u0026 Population program to support a $50M+ portfolio, and the UNCDF partnerships, policy, and communications team. ",
            //      "@type": "EducationGroup",
            //      "@id": "LxW2c7ek5iDOrc2j9RuMBf",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/5f89bb00f807828cbf4923518b163cbefea03c97-4291x5738.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Sravani Jandhyala",
            //      "url": "https://oiw.no/speaker/sravani-jandhyala-",
            //      "description": "Sravani, a UCLA alum, is an expert in curriculum development, evaluation, grant writing, and teaching. Her background includes grant management, organizational leadership, and positions at the Indo-American Cultural Center and Cal State Fullerton. Sravani is also an active Ektaa Center Board member and UCLA Alumni Scholarship volunteer.",
            //      "@type": "EducationGroup",
            //      "@id": "VJwDF6OhsypgL07DCTwsmJ",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/da85adc072c7590d895f4e141781ad8b1870aa3f-423x557.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Prasantha Devulapalli",
            //      "url": "https://oiw.no/speaker/prasantha-devulapalli",
            //      "description": "Prasantha, Founder \u0026 CEO of Coral Swans International, is a social entrepreneur passionate about empowering women and is a DEIB advocate. With an MBM from BI Norway, she excels in Negotiations, Leadership, and Business Development. Prasantha\u0027s 25+ experience includes managerial roles at Norfund, public offices and healthcare.",
            //      "@type": "EducationGroup",
            //      "@id": "VBlNaGbxeXMiFaeMV7FhQ2",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ef85a9e507ce7a55cdd4111e621005aa035dddf4-194x259.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Richa Chandra",
            //      "url": "https://oiw.no/speaker/richa-chandra",
            //      "description": "Richa Chandra, a certified and award-winning business coach, empowers entrepreneurs to find their calling and boost visibility. With 17+ years in business development, branding, and wealth management, she combines insight and action to deliver results. Richa was named Norway’s Most Aspirational Businesswoman by the Global Woman Club in 2019.",
            //      "@type": "EducationGroup",
            //      "@id": "VJwDF6OhsypgL07DCU1Fpq",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/76a5db3213d728c41a119d6b76f38920d93d769f-4180x4285.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "invest-in-women24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Impact",
            //      "Investment",
            //      "Panel"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] PROFIT HAS NO GENDER- Invest in Women, Accelerate Growth Sending event 'PROFIT HAS NO GENDER- Invest in Women, Accelerate Growth' to importer
            //{
            //  "name": "ClimateTech Investment Networking Event",
            //  "url": "https://oiw.no/event/climate-tech-networking-event24",
            //  "startDate": "2024-09-24T15:00:00.000Z",
            //  "localStartDate": "2024-09-24T17:00",
            //  "endDate": "2024-09-24T17:00:00.000Z",
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Sandwater",
            //      "url": "https://sandwater.com/",
            //      "@id": "9Zq9Ud6GKJqZ1M9eo7Y1vP",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/71ba99908993f3bcee672e338a97836d1b895e19-3038x494.png",
            //      "sameAs": [
            //        "https://sandwater.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://sandwater.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Startup Lab",
            //      "url": "https://startuplab.no/",
            //      "@id": "PD0p2pDN3sZp4xmEwnWvIO",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/2ddebbf91994d8f6f28b377360d1ea8ceea26a9e-4096x2152.png",
            //      "sameAs": [
            //        "https://startuplab.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://startuplab.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "World Fund",
            //      "url": "https://www.worldfund.vc/",
            //      "@id": "PD0p2pDN3sZp4xmEwnWvqa",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/3881e85911e1bb9db725b191eeb98b7accb119dd-2417x1186.png",
            //      "sameAs": [
            //        "https://www.worldfund.vc/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.worldfund.vc/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "ArcTern Ventures",
            //      "url": "https://www.arcternventures.com/",
            //      "@id": "aZRErKnzhCc3ad7aSGeMIA",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/1bedd1a7c8ca40b1c8cbba3918df8167f9effd8a-276x182.png",
            //      "sameAs": [
            //        "https://www.arcternventures.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.arcternventures.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Nysnø Climate Investments",
            //      "url": "https://www.nysnoinvest.no/",
            //      "@id": "aZRErKnzhCc3ad7aSGeMSo",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/1ca17d5967452413e42b688a541a6b324e5461f3-451x130.png",
            //      "sameAs": [
            //        "https://www.nysnoinvest.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.nysnoinvest.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "climate-tech-networking-event24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "ClimateTech",
            //      "Investment",
            //      "Networking"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] ClimateTech Investment Networking Event Sending event 'ClimateTech Investment Networking Event' to importer
            //{
            //  "name": "The Importance of a Positive Culture in Organizations",
            //  "url": "https://oiw.no/event/iese-business-school",
            //  "startDate": "2024-09-24T16:00:00.000Z",
            //  "localStartDate": "2024-09-24T18:00",
            //  "endDate": "2024-09-24T19:00:00.000Z",
            //  "description": "",
            //  "location": {
            //    "name": "REBEL",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": " Universitetsgata 2",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "@type": "Place",
            //    "@id": "7HzS7L2FZf6ukSalMIGCjt",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "IESE Busines School",
            //      "url": "https://www.iese.edu/",
            //      "@id": "7HzS7L2FZf6ukSalMIGHPR",
            //      "sameAs": [
            //        "https://www.iese.edu/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.iese.edu/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Anneloes Raes",
            //      "url": "https://oiw.no/speaker/anneloes-raes",
            //      "description": "Anneloes Raes is professor and Head of the Department of Managing People in Organizations at IESE Business School, and holder of the Puig Chair of Global Leadership Development at IESE. \n\nShe holds a PhD in Organizational Behavior from Maastricht University and a M.A. in Psychology at the Radboud University Nijmegen in the Netherlands. \n",
            //      "@type": "EducationGroup",
            //      "@id": "7HzS7L2FZf6ukSalMIGLlF",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/a8232ed97e5e6047050849168338413a62274631-400x400.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Christian Heiberg",
            //      "url": "https://oiw.no/speaker/christian-heiberg",
            //      "description": "Christian holds a BSc Business Administration from Copenhagen Business School, and an MBA from IESE Business School.   He has had a varied career in management positions in diverse industries like consulting, office furniture, venture capital, and real estate advisory – both in Europe and in Asia.  He is currently Managing Partner at VISINDI.",
            //      "@type": "EducationGroup",
            //      "@id": "27jkfP9EFXFTG07fKbJmCa",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/8574e7c114eb007cd45a69992444481ac2de4514-225x225.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Frida Rustøen",
            //      "url": "https://oiw.no/speaker/frida-rustøen",
            //      "description": "Frida holds a degree in Economics from University of Oslo (with an exchange year in Seuol), a MSc in Economics from NTNU, and an MBA from IESE Business School.   She worked several years in the banking sector, before embarking on several entrepreneurial projects. Currently a Principal at Idékapital,a technology-focused investment firm in Oslo.",
            //      "@type": "EducationGroup",
            //      "@id": "27jkfP9EFXFTG07fKbJuOe",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/e7e7f9abdc35bad1a0eb8d059062c614b2d73eb7-217x217.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Arve Utseth",
            //      "url": "https://oiw.no/speaker/arve-utseth",
            //      "description": "Arve works in the International Executive Programs department at IESE Business School, advising executives and L\u0026D professionals in organizations on professional and personal development of talent.\n\nHe has 25+ years of experience in management positions in Norway, Spain, and Switzerland - in a variety of sectors.\n\nArve holds an MBA from IESE.",
            //      "@type": "EducationGroup",
            //      "@id": "OQu0ThG8qWuUGjvjF15SL9",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/5b5a42d36eceb5a1cd5353d546d13cf6a5445d75-100x100.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "iese-business-school",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Networking",
            //      "Impact",
            //      "Business Conference"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] The Importance of a Positive Culture in Organizations Sending event 'The Importance of a Positive Culture in Organizations' to importer
            //{
            //  "name": "Innovating with Nature - optimal bio-resource utilization",
            //  "url": "https://oiw.no/event/lifesciencecluster24",
            //  "startDate": "2024-09-26T06:30:00.000Z",
            //  "localStartDate": "2024-09-26T08:30",
            //  "endDate": "2024-09-26T08:30:00.000Z",
            //  "description": "The future economy is biobased. How can we make the most of the biological resources available to us in a sustainable way?",
            //  "location": {
            //    "name": "Forskningsparken | Oslo Science Park",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9421409,
            //      "longitude": 10.7139813
            //    },
            //    "@type": "Place",
            //    "@id": "S8WVnhHTDNMkkNUC8Zjby7",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "The Life Science Cluster",
            //      "url": "https://thelifesciencecluster.no/",
            //      "@id": "EID7htTIDezvC7ki0XQIaQ",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/bf45ba5dbf0eb7a18f8a42206f2f08bc3679fe7b-127x114.svg",
            //      "sameAs": [
            //        "https://thelifesciencecluster.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://thelifesciencecluster.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Aggrator",
            //      "url": "https://aggrator.com/",
            //      "@id": "Q01A6nB0EzzzgHsi76KXoq",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/0eae27b1b265c93bd319a9f9fb906fcaec64caf9-2048x586.png",
            //      "sameAs": [
            //        "https://aggrator.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://aggrator.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Ard Innovation",
            //      "url": "https://ardinnovation.no/",
            //      "@id": "Q01A6nB0EzzzgHsi76KgAz",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/e0c6341b5d314389c4fc8e32e7f9e7d688aea871-196x192.jpg",
            //      "sameAs": [
            //        "https://ardinnovation.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://ardinnovation.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "NMBU",
            //      "url": "https://www.nmbu.no/",
            //      "@id": "Q01A6nB0EzzzgHsi76L5lg",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/aefbf30feb7f463a0bbf25a552842db05a581270-400x200.png",
            //      "sameAs": [
            //        "https://www.nmbu.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.nmbu.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Hanne Mette Dyrlie Kristensen",
            //      "url": "https://oiw.no/speaker/hanne-mette-dyrlie-kristensen",
            //      "description": "Hanne Mette D. Kristensen is Vice President Investor relations, business development and collaboration of The Life Science Cluster.\n\nShe has a solid background both as entrepreneur and biotech-CEO; she also has broad experience from clusters, both as an active member, and as member of the Board of Directors of OCC and NHT. ",
            //      "@type": "EducationGroup",
            //      "@id": "i25yJA0neSgT1BpxaIQoFU",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ff518dccb745c367908074fc0e527718343cc0db-1707x1830.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Nazli Pelin Kocatürk Schumacher",
            //      "url": "https://oiw.no/speaker/nazli-pelin-kocatürk-schumacher",
            //      "description": "Pelin is an associate professor at Water and Environmental Engineering Group at the Faculty of Science and Technology (RealTek). Her research in resource recovery lies in the essence of sustainable practices for resources and bio-waste management, which are some key elements for achieving a circular economy and UN Sustainable Development Goals.\n\n",
            //      "@type": "EducationGroup",
            //      "@id": "Q01A6nB0EzzzgHsi7CStTL",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/e74ed6c2593e86086f5ce286bb5e112ffea15484-880x1173.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Joe Amundsen",
            //      "url": "https://oiw.no/speaker/-joe-amundsen",
            //      "description": "Joe Amundsen is the CTO  at eVici, which is developing a digital solution for monitoring runoff from agriculture.",
            //      "@type": "EducationGroup",
            //      "@id": "U44O2fFzYvamVsAgbFTWbm",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/f24805113be439a0108c07a54356e64f0b568e52-1000x727.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "David Andrew Quist",
            //      "url": "https://oiw.no/speaker/david-andrew-quist",
            //      "description": "David Andrew is a microbiologist with a long-standing engagement in sustainability and producing high-impact research. He has served in various capacities to the Norwegian and European Environment Agencies, and scientific committees under the United Nations as Norwegian representative.",
            //      "@type": "EducationGroup",
            //      "@id": "gYQynWJerFZqsVq3m4B9OR",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/0f463ba066e886479b06f21560dd0dbbec763b32-1189x865.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Sileshi Gizachew Wubshet",
            //      "url": "https://oiw.no/speaker/sileshi-gizachew-wubshet",
            //      "description": "Sileshi is working as a Senior Scientist at the department of Raw material and Process, Nofima.\n\nA significant part of his research is focused on bio-analytical technologies enabling process optimization and product development (specifically, development of protein and peptide-based products). ",
            //      "@type": "EducationGroup",
            //      "@id": "63tJdbMRUF6pHm8nCprXfK",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/dc99e59a850da8495b3c72c178015a84547c0f0d-500x500.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "lifesciencecluster24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Sustainability",
            //      "Impact",
            //      "Seminar"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Innovating with Nature - optimal bio-resource utilization Sending event 'Innovating with Nature - optimal bio-resource utilization' to importer
            //{
            //  "name": "Build to Last: Inclusive product development",
            //  "url": "https://oiw.no/event/build-to-last24",
            //  "startDate": "2024-09-26T06:45:00.000Z",
            //  "localStartDate": "2024-09-26T08:45",
            //  "endDate": "2024-09-26T08:45:00.000Z",
            //  "description": "",
            //  "location": {
            //    "name": "Hausmanns Hus",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9168371,
            //      "longitude": 10.7526351
            //    },
            //    "@type": "Place",
            //    "@id": "VJwDF6OhsypgL07DCSux1v",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Skalar",
            //      "url": "https://www.skalar.no/",
            //      "@id": "PygGVB9TYoPQnjIow9kJGH",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/1f6665c9fdc6d1a92e23494b7cdc76ea60e79ab5-1375x327.png",
            //      "sameAs": [
            //        "https://www.skalar.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.skalar.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Inventas",
            //      "url": "https://www.inventas.no/",
            //      "@id": "clo3OfcAIt4G4RsnL1nocQ",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/64710145893d81720ac1e66188d1bade65ea497d-1600x620.png",
            //      "sameAs": [
            //        "https://www.inventas.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.inventas.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Maria Ævarsdottir",
            //      "url": "https://oiw.no/speaker/maria-ævarsdottir-",
            //      "description": "Maria is a Senior UX Designer at Manyone. With years of experience in UX design, she has worked with public actors such as Altinn and Deichman, creating user-friendly solutions. Additionally, she has worked as a usability engineer, working closely with medical device startups uncovering, documenting risk and helping find new solutions.",
            //      "@type": "EducationGroup",
            //      "@id": "EPBqwUaDv2CRt5j0fkCzkW",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/80af793787c5885887f6c0837e98624a35ec1bfd-1401x2048.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Kristian Aarseth",
            //      "url": "https://oiw.no/speaker/kristian-aarseth",
            //      "description": "Kristian Aarseth is an experienced design lead working at Aidn. Aidn provides health care workers with safe and easy to use tools that are empowering them in their work day. \nKristian has experience from NRK, startups and running a design studio. Working with big problems such as health care systems his focus is making the complex simple. And safe.",
            //      "@type": "EducationGroup",
            //      "@id": "JxpKUxUWSDPhcfPlpc7EeV",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/2c18e8c2fea5f2153654645a60403b7d42d793cb-2578x3892.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "build-to-last24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Community",
            //      "Scaling",
            //      "Talk"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Build to Last: Inclusive product development Sending event 'Build to Last: Inclusive product development' to importer
            //{
            //  "name": "Unleashing Innovation: Research \u0026 Startup Collaboration",
            //  "url": "https://oiw.no/event/nora24",
            //  "startDate": "2024-09-25T13:00:00.000Z",
            //  "localStartDate": "2024-09-25T15:00",
            //  "endDate": "2024-09-25T15:00:00.000Z",
            //  "description": "",
            //  "location": {
            //    "name": "Gründergarasjen (main entrance in Falbes gate)",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9211667,
            //      "longitude": 10.7319143
            //    },
            //    "@type": "Place",
            //    "@id": "mpUJMbgAdjArYK77FAR6aK",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Gründergarasjen",
            //      "url": "https://grundergarasjen.no/",
            //      "@id": "mpUJMbgAdjArYK77FAR9ES",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/3f2f4af3661d4f86f8e2b494ca6c8bca7ed1540e-1600x1600.png",
            //      "sameAs": [
            //        "https://grundergarasjen.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://grundergarasjen.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "NORA.ai",
            //      "url": "https://www.nora.ai/",
            //      "@id": "qpDpG6K3J1txFVqcm3EoKR",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/4c3948240b998a1e8cd25c2be200ff47ff136fb3-2153x881.png",
            //      "sameAs": [
            //        "https://www.nora.ai/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.nora.ai/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "NORA.startup",
            //      "url": "https://www.nora.ai/nora-startup/",
            //      "@id": "1y9vrm2LhG44NjaC6nMquv",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/9d462c8fa0ffeff65c41bf016401f0f68c87f6e8-3705x611.png",
            //      "sameAs": [
            //        "https://www.nora.ai/nora-startup/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.nora.ai/nora-startup/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Freyja Jørgensen",
            //      "url": "https://oiw.no/speaker/freyja-jørgensen",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "1y9vrm2LhG44NjaC6nGwJx",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/076dc0995002c32edfa56ef38d59da3f682f4650-500x500.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Birte Hansen",
            //      "url": "https://oiw.no/speaker/birte-hansen",
            //      "@type": "EducationGroup",
            //      "@id": "Pn48h1OtVfcepeo3p0fB43",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/949981d7f1e8e7d641d623456e070379f677f84a-400x400.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Jon Nordby",
            //      "url": "https://oiw.no/speaker/jon-nordby",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "xBB7iBjw6W7EaSR6v7rjAu",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/346e80be28a262fad825fcedeb7a1d9229e3f377-1578x1578.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Lars Traaholt Vågnes",
            //      "url": "https://oiw.no/speaker/lars-traaholt-vågnes",
            //      "description": "Lars is the co-founder and CEO of Simli, a company developing real-time AI avatars. He has 7 years of experience building and shipping AI models, including models that created value in the tens of millions of dollars for the maritime sector. Lars has founded two other generative AI companies: Nusic for generative music and Personate for AI video co",
            //      "@type": "EducationGroup",
            //      "@id": "i25yJA0neSgT1BpxaI2ls7",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/d71ff251c864f41806218356a853d4e733abbcec-1534x2045.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Ida Dahl",
            //      "url": "https://oiw.no/speaker/ida-dahl",
            //      "@type": "EducationGroup",
            //      "@id": "n7pDqjz27FZZSwsInM5hRR",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/d24057a3ec145515fecefbed7aea82ff1a5822a1-200x200.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Alise Danielle Midtfjord",
            //      "url": "https://oiw.no/speaker/alise-danielle-midtfjord",
            //      "@type": "EducationGroup",
            //      "@id": "mwmLYLkUsGOpo6Y0xowyRK",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/260cf532ba8505e8a47efa036c7e7072f16b4003-927x932.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "nora24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "DeepTech",
            //      "Community",
            //      "Fireside"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Unleashing Innovation: Research & Startup Collaboration Sending event 'Unleashing Innovation: Research & Startup Collaboration' to importer
            //{
            //  "name": "Oslo Business Forum",
            //  "url": "https://oiw.no/event/oslo-business-forum24",
            //  "startDate": "2024-09-25T08:30:00.000Z",
            //  "localStartDate": "2024-09-25T10:30",
            //  "endDate": "2024-09-26T15:00:00.000Z",
            //  "description": "LinkedIn: https://www.linkedin.com/company/10595563\nFacebook: https://www.facebook.com/Obforum\nInstagram: https://www.instagram.com/obforum/",
            //  "location": {
            //    "name": "NOVA Spektrum ",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "@type": "Place",
            //    "@id": "Yfy6cE63t2HF6HBDbqBtju",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Oslo Business Forum",
            //      "url": "https://www.obforum.com/2024",
            //      "@id": "0b5f6666-2d62-49fe-8f9b-2afb1ef82e19",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/4935ba007658e0977ce5fdb51167cf629c343047-225x225.png",
            //      "sameAs": [
            //        "https://www.obforum.com/2024"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.obforum.com/2024"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Adam Grant",
            //      "url": "https://oiw.no/speaker/adam-grant",
            //      "description": "Adam Grant isn’t just an organizational psychologist — he\u0027s a revolution in the way we approach success. Imagine completely shifting your perspective on work, creativity, and ambition. That\u0027s what Adam does. Best known for his groundbreaking book, Think Again, and his captivating TED Talks, which millions have tuned into.",
            //      "@type": "EducationGroup",
            //      "@id": "75hF6u0k3iHYTKl66Om44A",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/11150f26b5d096242c310f3f50e7d97fe0a78b3d-1200x1200.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Brené Brown",
            //      "url": "https://oiw.no/speaker/brené-brown",
            //      "description": "Dr. Brené Brown is a research professor at the University of Houston, where she holds the Huffington Foundation Endowed Chair at the Graduate College of Social Work.\n\nBrené has spent the past two decades studying courage, vulnerability, shame, and empathy. She is the author of six #1 New York Times best sellers.",
            //      "@type": "EducationGroup",
            //      "@id": "FyAHGzCGYvVljtOszkMoMm",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ee7f522ab9fad8f9cb42f6b195909e1a6c5a68d2-1200x1200.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Mo Gawdat",
            //      "url": "https://oiw.no/speaker/mo-gawdat",
            //      "description": "After working for IBM and Microsoft, Mo landed a job at Google and helped start the platform in more than 50 emerging markets across the Middle East, Africa and Eastern Europe. He then joined Google [X] as chief business officer and lived at the cutting edge of technology.",
            //      "@type": "EducationGroup",
            //      "@id": "FyAHGzCGYvVljtOszkNxcu",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/1f2cb249f4de446d52fe1b3b176e320dcf2217b2-1200x1200.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Sanna Marin",
            //      "url": "https://oiw.no/speaker/sanna-marin",
            //      "description": "Sanna Marin, esteemed as a global example of dynamic and progressive leadership, has held the distinguished honor of being one of the world’s youngest serving prime ministers in the world and Finland’s youngest ever. With a leadership style characterized by adaptability, inclusivity, and innovation, Marin\u0027s insights are profound and relevant. ",
            //      "@type": "EducationGroup",
            //      "@id": "azBm0ox0gTIvJxtHPtQ5k4",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/697be682cd68907876d9f8e2b86b4be751c3c2b9-1200x1200.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Ole Gunnar Solsjkær",
            //      "url": "https://oiw.no/speaker/ole-gunnar-solsjkær",
            //      "description": "Solskjær has become synonymous with visionary leadership, resilience, and the art of nurturing talent. His keynote will delve into the core principles that have guided his approach to building winning teams and fostering environments where talent flourishes.",
            //      "@type": "EducationGroup",
            //      "@id": "FyAHGzCGYvVljtOszkP3w8",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/4b7c7836857127c1eca4386236cb1dc980dd0464-1200x1200.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Howard Yu",
            //      "url": "https://oiw.no/speaker/howard-yu",
            //      "description": "Award-winning author of LEAP \u0026 Director of IMD’s Advanced Management Program, Yu brings expertise to Oslo Business Forum. From Hong Kong to Harvard, his work bridges academia and global business, recognized by EFMD \u0026 Thinkers50, influencing top companies.",
            //      "@type": "EducationGroup",
            //      "@id": "azBm0ox0gTIvJxtHPtSWKd",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/1129671dd4a2aa3ef495e7f3ca00e508d020428e-1200x1200.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Erin Meyer",
            //      "url": "https://oiw.no/speaker/erin-meyer",
            //      "description": "Erin Meyer redefines leadership across cultures. As the world becomes increasingly interconnected, Erin\u0027s insights are more crucial than ever. Her collaboration with Reed Hastings on \u0027No Rules Rules\u0027 and her solo masterpiece \u0027The Culture Map\u0027 have set new standards for managing multicultural teams and fostering innovation. ",
            //      "@type": "EducationGroup",
            //      "@id": "azBm0ox0gTIvJxtHPtU0Qi",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/694681b5529a2550e73683d9363b13e5b7771218-1200x1200.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Peter Hinssen",
            //      "url": "https://oiw.no/speaker/peter-hinssen",
            //      "description": "Peter is a world class thought leader on technological evolution, innovation strategy and adaptive leadership. He is a top ranked keynote speaker, bestselling author, business school lecturer, LinkedIn Top Voice, serial entrepreneur, trusted board member and passionate startup investor. ",
            //      "@type": "EducationGroup",
            //      "@id": "FyAHGzCGYvVljtOszkSHQS",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/0dbdf5914046004d0a5c7a5d802e56ff1eddc818-1200x1200.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Morten Hansen",
            //      "url": "https://oiw.no/speaker/morten-hansen",
            //      "description": "Formerly a professor at Harvard Business School and INSEAD (France), professor Hansen holds a PhD from Stanford Business School, where he was a Fulbright scholar. His academic research has won several prestigious awards, and he is ranked one of the world’s most influential management thinkers by Thinkers50. ",
            //      "@type": "EducationGroup",
            //      "@id": "75hF6u0k3iHYTKl66Ou9oo",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ba7ef2f6a61c06bc933e1300459d72160c06f079-1200x1200.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Mikko Hyppönen",
            //      "url": "https://oiw.no/speaker/mikko-hyppönen",
            //      "description": "Mikko Hypponen is a global security expert, speaker, and author. He works as the Chief Research Officer at WithSecure and as the Principal Research Advisor at F-Secure.\n\nHyppönen has written about his research in the New York Times, Wired, and Scientific American, and appears frequently on international TV. ",
            //      "@type": "EducationGroup",
            //      "@id": "FyAHGzCGYvVljtOszkUh5k",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/3bf2a5be31fa34b92843f4b8a407713423079bcd-1200x1200.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "oslo-business-forum24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Networking",
            //      "Impact",
            //      "Business Conference"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Oslo Business Forum Sending event 'Oslo Business Forum' to importer
            //{
            //  "name": "Science Impact - tomorrow\u0027s solutions start today",
            //  "url": "https://oiw.no/event/science-impact24",
            //  "startDate": "2024-09-25T06:00:00.000Z",
            //  "localStartDate": "2024-09-25T08:00",
            //  "endDate": "2024-09-25T15:00:00.000Z",
            //  "description": "Spend a day with great science, innovation and networking opportunities at this meeting place for academia, the institute sector, the public sector, start-ups and industry. Topic: artificial intelligence within life sciences and energy. ",
            //  "location": {
            //    "name": "Oslo Science Park",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9421409,
            //      "longitude": 10.7139813
            //    },
            //    "@type": "Place",
            //    "@id": "JmK82UtxrwtkASwLHg6FUp",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "UiO Growth House",
            //      "url": "https://www.uio.no/english/research/interfaculty-research-areas/growth-house/",
            //      "@id": "JmK82UtxrwtkASwLHg6M79",
            //      "sameAs": [
            //        "https://www.uio.no/english/research/interfaculty-research-areas/growth-house/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.uio.no/english/research/interfaculty-research-areas/growth-house/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Inven2",
            //      "@id": "EHx17Nf6QXcwkGWn1epM2D",
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "SINTEF",
            //      "@id": "EHx17Nf6QXcwkGWn1epTN0",
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Oslo University Hospital",
            //      "@id": "JmK82UtxrwtkASwLHg6Om8",
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "City of Oslo",
            //      "@id": "JmK82UtxrwtkASwLHg6PZi",
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "LMI",
            //      "@id": "JmK82UtxrwtkASwLHg6Ppx",
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Oslo Cancer Cluster",
            //      "@id": "EHx17Nf6QXcwkGWn1epcSd",
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "UiO:Energy and Environment",
            //      "@id": "JmK82UtxrwtkASwLHg6Qy3",
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "dScience",
            //      "@id": "JmK82UtxrwtkASwLHg6RcE",
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Faculty of Medicine",
            //      "@id": "fyxH4pd32gZCk81dyDk1ZV",
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Faculty of Mathematics and Natural Sciences",
            //      "@id": "JmK82UtxrwtkASwLHg6W63",
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Faculty of Social Sciences",
            //      "@id": "JmK82UtxrwtkASwLHg6X7J",
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Oslo Science Park",
            //      "@id": "EHx17Nf6QXcwkGWn1eqBRd",
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "The Life Science Cluster",
            //      "url": "https://thelifesciencecluster.no/",
            //      "@id": "CmlaH7ihJVXUEwGX2EUB0Q",
            //      "sameAs": [
            //        "https://thelifesciencecluster.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://thelifesciencecluster.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Klas H. Pettersen",
            //      "url": "https://oiw.no/speaker/klas-h-pettersen",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "Q01A6nB0EzzzgHsi7679Ox",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/d5334edfc28308584d81fd4ea61e7ddd4853213f-700x875.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Lilja Øvrelid",
            //      "url": "https://oiw.no/speaker/lilja-øvrelid",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "i25yJA0neSgT1BpxaIDi2j",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ed4c17fb9ac23156b3f99cae6d9c1be29d64c756-1920x2880.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Jonas Verhellen",
            //      "url": "https://oiw.no/speaker/jonas-verhellen",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "Q01A6nB0EzzzgHsi767MLN",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/7aaa9aec3ab4ba2d66454fa74a2d7a71c8e66813-150x200.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Christine Demmo-Bru",
            //      "url": "https://oiw.no/speaker/christine-demmo-bru",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "Q01A6nB0EzzzgHsi767TOs",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/f1d9faba7c706d0e949aaf0e60fe216d531c17a9-544x598.webp",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "John Burkhart",
            //      "url": "https://oiw.no/speaker/john-burkhart",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "i25yJA0neSgT1BpxaIDyIe",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/b08e3894923a70b2813c349b3c5dd03e8a5978d7-150x212.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Francesca Watson",
            //      "url": "https://oiw.no/speaker/francesca-watson",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "Q01A6nB0EzzzgHsi767eEF",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/59e0352fe73d63690819f0ade7ac2e3a25604f51-4052x4057.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "science-impact24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "HealthTech",
            //      "ClimateTech",
            //      "Conference"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Science Impact - tomorrow's solutions start today Sending event 'Science Impact - tomorrow's solutions start today' to importer
            //{
            //  "name": "Pioneering with Purpose: Can It Be Done?",
            //  "url": "https://oiw.no/event/sap-scaleups24",
            //  "startDate": "2024-09-26T13:00:00.000Z",
            //  "localStartDate": "2024-09-26T15:00",
            //  "endDate": "2024-09-26T14:00:00.000Z",
            //  "description": "",
            //  "location": {
            //    "name": "SAP Office - Universitetsgata 1, Oslo",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 60.3663683,
            //      "longitude": 3.9940584
            //    },
            //    "@type": "Place",
            //    "@id": "3bSeV1kXTHmPOpL581EY5e",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Grow for Scaleups ",
            //      "url": "https://www.sap.com/products/erp/grow/scaleups.html",
            //      "@id": "gtrSyynP7P1x0DLKmnxdkV",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/9de08c784359f32a058f657d0ac1952f40cae4a7-8580x1192.png",
            //      "sameAs": [
            //        "https://www.sap.com/products/erp/grow/scaleups.html"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.sap.com/products/erp/grow/scaleups.html"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "SAP",
            //      "url": "https://www.sap.com/products/erp/grow/scaleups.html",
            //      "@id": "bJU0rnud4tYDJnYMKGtBSJ",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/3e08f7a5932f9da5a3e3ae48c3f051b3899ace69-1719x851.png",
            //      "sameAs": [
            //        "https://www.sap.com/products/erp/grow/scaleups.html"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.sap.com/products/erp/grow/scaleups.html"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "William Dennett",
            //      "url": "https://oiw.no/speaker/william-dennett",
            //      "description": "CDO of FREYR Battery, a scaleup within green technology. Building, managing and leveraging the NYSE-listed industrial scaleup\u0027s digital and data assets to drive growth, innovation, and competitive advantage. Overseeing the development and continuous improvement of digital architectures for managing industrial manufacturing assets - Industry 4.0",
            //      "@type": "EducationGroup",
            //      "@id": "xBB7iBjw6W7EaSR6vAQvBg",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/8aca7416a9850a95bda1d43b59017e01b197a16a-129x129.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Agnes Lusti",
            //      "url": "https://oiw.no/speaker/agnes-lusti",
            //      "description": "Agnes blends strategic insight with hands-on expertise in every endeavor. As a RISE Solution Advisor, she drives digital transformations and enhances digital investments, ensuring sustainable growth and competitive advantage. Leveraging her passion for innovation, Agnes helps companies harness cutting-edge technology to stay ahead of the curve. ",
            //      "@type": "EducationGroup",
            //      "@id": "rYI3i8J9scbOv3VH588FwF",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/37a150859d3e5b631eb81fa0c0d62d9ed3a6855f-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Frederic Pascal",
            //      "url": "https://oiw.no/speaker/frederic-pascal",
            //      "description": "Global Head of Growth at SAP. Scaling up Sales operations, skilled in Business Development, Management, Software as a Service (SaaS), Customer Experience, and Business Process Improvement.",
            //      "@type": "EducationGroup",
            //      "@id": "Vm1Iy7AzZo4BZhL7FmbLFm",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/fd50b4992280a4e6500579d4c460b4241bdde6b9-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "sap-scaleups24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Scaling",
            //      "Impact",
            //      "Fireside"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Pioneering with Purpose: Can It Be Done? Sending event 'Pioneering with Purpose: Can It Be Done?' to importer
            //{
            //  "name": "Norselab Impact Day 2024: Pioneering Meaningful Investing",
            //  "url": "https://oiw.no/event/norselab-impact-day-24",
            //  "startDate": "2024-09-25T11:00:00.000Z",
            //  "localStartDate": "2024-09-25T13:00",
            //  "endDate": "2024-09-25T15:00:00.000Z",
            //  "description": "Norselab Impact Day 2024: Pioneering Meaningful Investing will bring together the impact investing community and visionary founders for an inspiring event delving into the pioneering efforts happening in this space.",
            //  "location": {
            //    "name": "Ingensteds",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9013827,
            //      "longitude": 10.7367906
            //    },
            //    "@type": "Place",
            //    "@id": "v3431n7IAaIx7DOZmWVE3l",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Investinor",
            //      "url": "https://investinor.no",
            //      "@id": "JmK82UtxrwtkASwLHg5tXv",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/2864dc8b6817ae74eb4e138ab908438b58bee3de-100x50.svg",
            //      "sameAs": [
            //        "https://investinor.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://investinor.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Norselab",
            //      "url": "https://norselab.com",
            //      "@id": "JmK82UtxrwtkASwLHg5uLV",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/41d0fcda6b8e23219abe757431d4c011e65f9283-1620x440.png",
            //      "sameAs": [
            //        "https://norselab.com"
            //      ],
            //      "gogo": {
            //        "webpage": "https://norselab.com"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Capricorn Investment Group",
            //      "url": "https://capricornllc.com",
            //      "@id": "FNKpwkKRimvSChfWMsKOZj",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/dbed43f16aed791fd9701d9666abf61a87bcfabd-263x47.svg",
            //      "sameAs": [
            //        "https://capricornllc.com"
            //      ],
            //      "gogo": {
            //        "webpage": "https://capricornllc.com"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Maria de Perlinghi",
            //      "url": "https://oiw.no/speaker/maria-de-perlinghi",
            //      "@type": "EducationGroup",
            //      "@id": "v3431n7IAaIx7DOZmWT67v",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/82e3dfc437d9422cfc68391b45147b2d5bfaf893-580x562.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Christian Rosenholm",
            //      "url": "https://oiw.no/speaker/christian-rosenholm",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "LxW2c7ek5iDOrc2j9PcDkX",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/3611e7daf055b57bdc32f84d368b73056ed1896d-580x562.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Signe Sørensen",
            //      "url": "https://oiw.no/speaker/signe-sorensen",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "LxW2c7ek5iDOrc2j9PcEf9",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/8477d6519a68a09dc13d44c110ee1a41f22683ff-250x250.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Matthew Coffay",
            //      "url": "https://oiw.no/speaker/matthew-coffay",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "LxW2c7ek5iDOrc2j9Pe3wI",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/b2d1a99f917a530ab4d31bb5177070d09845ca6a-250x250.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Michaela Edwards",
            //      "url": "https://oiw.no/speaker/michaela-edwards",
            //      "@type": "EducationGroup",
            //      "@id": "jjBq4X6gdc6IuElj7pHGNr",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/450064da2ac1eaba034d1e0a86b48e54285938b3-580x562.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Tom Hestnes",
            //      "url": "https://oiw.no/speaker/tom-hestnes",
            //      "@type": "EducationGroup",
            //      "@id": "v3431n7IAaIx7DOZmWV189",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/de1f0cbc99782a019df375b48e217e068ad469e1-580x562.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Karl Olav Sørensen",
            //      "url": "https://oiw.no/speaker/karl-olav-sorensen",
            //      "description": "",
            //      "@type": "EducationGroup",
            //      "@id": "jjBq4X6gdc6IuElj7pHOLh",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/b4087e51a934f1f959a4f224ac406c88374d9aaf-250x250.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Linn Hege Aune",
            //      "url": "https://oiw.no/speaker/linn-hege-aune",
            //      "@type": "EducationGroup",
            //      "@id": "jjBq4X6gdc6IuElj7uFF5T",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/40af3953be7137435c7ba92a99a79dd68f0e9cd0-290x281.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Brynjar Bustnes",
            //      "url": "https://oiw.no/speaker/brynjar-bustnes",
            //      "@type": "EducationGroup",
            //      "@id": "Q01A6nB0EzzzgHsi76bU1k",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/f2f45ea87831bb3d7f70aaab014733dc8e5a8108-250x250.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Andreas Friis",
            //      "url": "https://oiw.no/speaker/andreas-friis",
            //      "@type": "EducationGroup",
            //      "@id": "NO7Mjjgfso2MMqbm3fuwGj",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ec6399e62898765b854c5b0309c4ee4f4100d55f-250x250.png",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "norselab-impact-day-24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Investment",
            //      "Impact",
            //      "Impact Day"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Norselab Impact Day 2024: Pioneering Meaningful Investing Sending event 'Norselab Impact Day 2024: Pioneering Meaningful Investing' to importer
            //{
            //  "name": "Scaling With AI - by Microsoft, Advania \u0026 Brækhus",
            //  "url": "https://oiw.no/event/scaling-with-ai24",
            //  "startDate": "2024-09-24T12:00:00.000Z",
            //  "localStartDate": "2024-09-24T14:00",
            //  "endDate": "2024-09-24T13:30:00.000Z",
            //  "description": "Brækhus:\n- https://www.linkedin.com/company/br%C3%A6khus-advokatfirma-da/\n- https://www.facebook.com/braekhusadvokatfirma/\n- https://www.instagram.com/braekhusadvokatfirma/\n\nAdvania\n- https://www.facebook.com/www.advania.no\n- https://www.linkedin.com/company/advania-norge\n- https://www.instagram.com/advanianorge/\n\nMicrosoft Norge:\nhttps://www.linkedin.com/company/microsoft/\nhttps://twitter.com/microsoft\n",
            //  "location": {
            //    "name": "Mesh Youngstorget",
            //    "address": {
            //      "@type": "PostalAddress",
            //      "addressCountry": "NO",
            //      "addressLocality": "Oslo",
            //      "streetAddress": "",
            //      "timezone": "Europe/Oslo"
            //    },
            //    "geo": {
            //      "@type": "GeoCoordinates",
            //      "latitude": 59.9139676,
            //      "longitude": 10.7439046
            //    },
            //    "@type": "Place",
            //    "@id": "z1rBeBjb56T7I90dk3QeTU",
            //    "internal": {
            //      "source": "osloInnovationWeek",
            //      "hints": {
            //        "location": "approximate"
            //      }
            //    }
            //  },
            //  "performers": [
            //    {
            //      "name": "Oslo Innovation Week",
            //      "address": {
            //        "@type": "PostalAddress",
            //        "addressCountry": "NO"
            //      },
            //      "@type": "CreativeWork",
            //      "@id": "oiw",
            //      "sameAs": [
            //        "https://oiw.no"
            //      ],
            //      "gogo": {
            //        "webpage": "https://oiw.no"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "conference"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Advania",
            //      "url": "https://www.advania.com/",
            //      "@id": "j0P7aBo0bkM6HAdsmiRFPQ",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/e111a4ed7667652898508f124622f50401bcca44-3508x1692.png",
            //      "sameAs": [
            //        "https://www.advania.com/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.advania.com/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Brækhus Advokatfirma",
            //      "url": "https://braekhus.no/",
            //      "@id": "j0P7aBo0bkM6HAdsmiRFtM",
            //      "sameAs": [
            //        "https://braekhus.no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://braekhus.no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Microsoft",
            //      "url": "https://www.microsoft.com/nb-no/",
            //      "@id": "3bSeV1kXTHmPOpL580ssww",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/0ed486b7a8ac423ea935793035237a87fd4479bc-1536x688.png",
            //      "sameAs": [
            //        "https://www.microsoft.com/nb-no/"
            //      ],
            //      "gogo": {
            //        "webpage": "https://www.microsoft.com/nb-no/"
            //      },
            //      "internal": {
            //        "gogoType": [
            //          "host"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Pamir Ehsas",
            //      "url": "https://oiw.no/speaker/pamir-ehsas",
            //      "description": "Pamir Ehsas is a tech-lawyer at Brækhus, specializing in AI, cybersecurity, and privacy. He holds the prestigious CIPP/E privacy certification and serves as an AI specialist board member at the Norwegian Computer Society. Pamir advises large multinational IT corporations and tech scale-ups on IT law, financing, M\u0026A, and compliance. As a former tech",
            //      "@type": "EducationGroup",
            //      "@id": "LMDtzdFP0V5rPqBZv18r9X",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/ceb15b0410670a43614f00df4924f6df33a526c8-300x300.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Garzai Ehsas",
            //      "url": "https://oiw.no/speaker/garzai-ehsas",
            //      "description": "With extensive experience in cloud technology and artificial intelligence, Garzai has been involved in introducing Microsoft Azure and AI services to the Norwegian market. As an experienced architect, he has guided customers through their migration journeys to Azure and has been passionately interested in AI. He has led the adoption of Microsoft Co",
            //      "@type": "EducationGroup",
            //      "@id": "jY3ou9bQYCSswQTCA4kSc2",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/3f976abcf789e163f0d1b16417f6a0cae3898cf6-300x300.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    },
            //    {
            //      "name": "Yashoda Singh",
            //      "url": "https://oiw.no/speaker/yashoda-singh",
            //      "description": "Yashoda Singh works with major banks and financial services and media houses in Norway.\nShe is actively involved in building the AI community and a public speaker, presenting both nationally and internationally. Recognized among the top 50 tech women in Norway and the top 100 Data \u0026 AI professionals in the Nordics.",
            //      "@type": "EducationGroup",
            //      "@id": "LMDtzdFP0V5rPqBZv19ulH",
            //      "image": "https://cdn.sanity.io/images/h1jmcyiv/production/8b0755888e55fbde7355a6b481667d262c4c7e8f-800x800.jpg",
            //      "internal": {
            //        "gogoType": [
            //          "speaker"
            //        ],
            //        "source": "osloInnovationWeek"
            //      }
            //    }
            //  ],
            //  "@type": "Event",
            //  "@id": "scaling-with-ai24",
            //  "image": {
            //    "@type": "ImageObject",
            //    "contentUrl": "https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg"
            //  },
            //  "internal": {
            //    "gogoType": [
            //      "Scaling",
            //      "SaaS",
            //      "Panel"
            //    ],
            //    "source": "osloInnovationWeek"
            //  }
            //},
            //[com.mobilitus.attractionscmd.oiw.OIWScraper.wrapEnvelope(OIWScraper.java:75)] Scaling With AI - by Microsoft, Advania & Brækhus Sending event 'Scaling With AI - by Microsoft, Advania & Brækhus' to importer
            //Disconnected from the target VM, address: '127.0.0.1:52083', transport: 'socket'
            //
            //Process finished with exit code 0onPair.getValue());
        }
        EventList result = EventList.create(jsonPair.getValue());
        List<SchemaEvent> schemaEvents = result.toSchemaEvents();
        logger.info("\n\n\n\n");
        if (schemaEvents != null)
        {
            for (SchemaEvent schemaEvent : schemaEvents)
            {

                System.out.println(StrUtil.formatAsJson(schemaEvent.toJson()) + ",");

                  PusherMessage msg = wrapEnvelope(schemaEvent, schemaEvent.getId());
                 toEventCreator.sendAsObj(schemaEvent.getId() + DataSource.osloInnovationWeek.name(), MessageType.eventUpdated.name(), msg.toJson());

//                 System.out.println("");
            }
        }
        toEventCreator.flush();
    }

    private PusherMessage wrapEnvelope(SchemaEvent ev, String id)
    {
        PusherMessage msg = new PusherMessage();

        msg.setSubject(ev.getName());

        msg.add(PropertyType.eventID.name(), ev.getId());

        msg.add(PropertyType.site.name(),  ev.getImportSource());
        msg.setWhen(ev.getStartDate());

        msg.setPayload(ev.toJson());
        msg.add("importerid", id);


        logger.info(ev.getName() + " Sending event '" + ev.getName() + "' to importer");
        return msg;
    }


}
