# Dev notes

## Corrupted Gradle cache

Please run this script if the build fails for 
unknown reasons spitting out errors unrelated with
the change done.
The script stops Gradle daemons and clears local caches.

```shell
./fix-grade.sh
```

## Debug test

In case that the build fails because in a test an http
request fail, and you are unsure on what is going on,
consider adding the following test:

```kotlin
// Temporary debug snippet: prints the raw HTTP response from a MockMvc request
// Use this to inspect response body, status, and content type when a test fails unexpectedly.
// Not meant to be committed as a permanent test.
@Test 
fun `DEBUG - GET http prints raw response`() {
    // Use this snippet to inspect the response of a MockMvc request
    val result = mockMvc.get("/api/v1/my-endpoint") 
        .andReturn() 
    
    println("Status: ${result.response.status}") 
    println("Content-Type: ${result.response.contentType}") 
    println("Response body:\n${result.response.contentAsString}") 
}
```