package Demo

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import Demo.Data._
import scala.concurrent.duration._
import scala.util.Random

class LoginTest extends Simulation {

  // Configuración HTTP base
  val httpConf = http
    .baseUrl(url)
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Gatling Load Testing")

  // Feeders para datos dinámicos
  val contactDataFeeder = Iterator.continually(Map(
    "firstName" -> contactFirstNames(Random.nextInt(contactFirstNames.length)),
    "lastName" -> contactLastNames(Random.nextInt(contactLastNames.length)),
    "email" -> s"contact${Random.nextInt(10000)}@test${Random.nextInt(100)}.com",
    "phone" -> contactPhones(Random.nextInt(contactPhones.length)),
    "birthdate" -> contactBirthdates(Random.nextInt(contactBirthdates.length)),
    "street1" -> contactStreet1(Random.nextInt(contactStreet1.length)),
    "street2" -> contactStreet2(Random.nextInt(contactStreet2.length)),
    "city" -> contactCities(Random.nextInt(contactCities.length)),
    "stateProvince" -> contactStatesProvinces(Random.nextInt(contactStatesProvinces.length)),
    "postalCode" -> contactPostalCodes(Random.nextInt(contactPostalCodes.length)),
    "country" -> contactCountries(Random.nextInt(contactCountries.length))
  ))

  // Historia de Usuario 1: Scenario de Login Exitoso y Reutilización del Token
  val loginSuccessfulScenario = scenario("Historia 1 - Login Exitoso y Acceso a Contactos")
    .exec(session => {
      println("Iniciando login con credenciales válidas...")
      println(s"Email: $email")
      println(s"Password: $password")
      session
    })
    .exec(
      http("Login Exitoso")
        .post("/users/login")
        .body(StringBody(s"""{"email":"$email","password":"$password"}"""))
        .check(status.is(200))
        .check(jsonPath("$.token").exists.saveAs("authToken"))
    )
    .exec(session => {
      val token = session("authToken").as[String]
      println(s"Token obtenido: ${token.take(20)}...")
      session
    })
    .pause(1)
    .exec(session => {
      println("Probando acceso a /contacts con token...")
      session
    })
    .exec(
      http("Reutilizar Token - Obtener Contactos")
        .get("/contacts")
        .header("Authorization", "Bearer ${authToken}")
        .check(status.is(200))
        .check(jsonPath("$").exists)
    )
    .exec(session => {
      println("Acceso exitoso a contactos!")
      session
    })

  // Historia de Usuario 1: Scenario de Login con Credenciales Inválidas
  val loginInvalidCredentialsScenario = scenario("Historia 1 - Login con Credenciales Inválidas")
    .exec(
      http("Login con Email Inválido")
        .post("/users/login")
        .body(StringBody(s"""{"email":"$invalidEmail","password":"$password"}"""))
        .check(status.is(401))
        .check(jsonPath("$.message").is("Incorrect email or password"))
    )
    .exec(session => {
      println("Error 401 recibido correctamente para email inválido")
      session
    })
    .pause(1)
    .exec(session => {
      println("Probando login con password inválido...")
      session
    })
    .exec(
      http("Login con Password Inválido")
        .post("/users/login")
        .body(StringBody(s"""{"email":"$email","password":"$invalidPassword"}"""))
        .check(status.is(401))
        .check(jsonPath("$.message").is("Incorrect email or password"))
    )
    .exec(session => {
      println("Error 401 recibido correctamente para password inválido")
      session
    })

  // Historia de Usuario 1: Scenario de Validación de Formato de Email
  val loginMalformedEmailScenario = scenario("Historia 1 - Validación Formato Email")
    .exec(
      http("Login con Email Malformado")
        .post("/users/login")
        .body(StringBody(s"""{"email":"$malformedEmail","password":"$password"}"""))
        .check(status.in(400, 422))
        .check(jsonPath("$.message").exists)
    )
    .exec(session => {
      println("Error de validación recibido correctamente")
      session
    })

  // Historia de Usuario 1: Scenario de Campos Requeridos
  val loginRequiredFieldsScenario = scenario("Historia 1 - Validación Campos Requeridos")

    .exec(
      http("Login sin Email")
        .post("/users/login")
        .body(StringBody(s"""{"password":"$password"}"""))
        .check(status.is(400))
    )
    .exec(session => {
      println("Error recibido correctamente para falta de email")
      session
    })
    .pause(1)
    .exec(session => {
      println("Probando login sin password...")
      session
    })
    .exec(
      http("Login sin Password")
        .post("/users/login")
        .body(StringBody(s"""{"email":"$email"}"""))
        .check(status.is(400))
    )
    .exec(session => {
      println("Error recibido correctamente para falta de password")
      session
    })

  // Historia de Usuario 2: Scenario de Creación Masiva de Contactos
  val createContactScenario = scenario("Historia 2 - Creación Masiva de Contactos")
    .exec(session => {
      println("Iniciando login para crear contactos...")
      session
    })
    // Primero hacer login para obtener el token
    .exec(
      http("Login para Crear Contactos")
        .post("/users/login")
        .body(StringBody(s"""{"email":"$email","password":"$password"}"""))
        .check(status.is(200))
        .check(jsonPath("$.token").exists.saveAs("authToken"))
    )
    .exec(session => {
      val token = session("authToken").as[String]
      println(s"Token obtenido para crear contactos: ${token.take(20)}...")
      session
    })
    .pause(1)
    .feed(contactDataFeeder)
    .exec(session => {
      val firstName = session("firstName").as[String]
      val lastName = session("lastName").as[String]
      val email = session("email").as[String]
      val city = session("city").as[String]
      val country = session("country").as[String]
      println(s"Creando contacto: $firstName $lastName")
      println(s"Email: $email")
      println(s"Ubicación: $city, $country")
      session
    })
    .exec(
      http("Crear Contacto")
        .post("/contacts")
        .header("Authorization", "Bearer ${authToken}")
        .body(StringBody("""{
          "firstName":"${firstName}",
          "lastName":"${lastName}",
          "birthdate":"${birthdate}",
          "email":"${email}",
          "phone":"${phone}",
          "street1":"${street1}",
          "street2":"${street2}",
          "city":"${city}",
          "stateProvince":"${stateProvince}",
          "postalCode":"${postalCode}",
          "country":"${country}"
        }"""))
        .check(status.is(201))
        .check(jsonPath("$._id").exists.saveAs("contactId"))
        .check(jsonPath("$.firstName").is("${firstName}"))
        .check(jsonPath("$.lastName").is("${lastName}"))
        .check(jsonPath("$.email").is("${email}"))
        .check(jsonPath("$.city").is("${city}"))
    )
    .exec(session => {
      val contactId = session("contactId").as[String]
      println(s"Contacto creado exitosamente con ID: $contactId")
      session
    })
    .pause(1)
    // Verificar que el contacto aparece en la lista
    .exec(session => {
      println("Verificando que el contacto aparece en la lista...")
      session
    })
    .exec(
      http("Verificar Contacto en Lista")
        .get("/contacts")
        .header("Authorization", "Bearer ${authToken}")
        .check(status.is(200))
        .check(jsonPath("$[?(@._id == '${contactId}')]").exists)
    )
    .exec(session => {
      println("Contacto verificado en la lista!")
      session
    })

  // Historia de Usuario 2: Scenario de Validación de Campos Requeridos en Contactos
  val createContactValidationScenario = scenario("Historia 2 - Validación Creación Contactos")
    .exec(session => {
      println("Iniciando pruebas de validación...")
      session
    })
    .exec(
      http("Login para Validaciones")
        .post("/users/login")
        .body(StringBody(s"""{"email":"$email","password":"$password"}"""))
        .check(status.is(200))
        .check(jsonPath("$.token").exists.saveAs("authToken"))
        .check(jsonPath("$.user.email").is(email))
    )
    .pause(1)
    .exec(session => {
      println("Probando crear contacto sin firstName...")
      session
    })
    .exec(
      http("Crear Contacto sin FirstName (Campo Requerido)")
        .post("/contacts")
        .header("Authorization", "Bearer ${authToken}")
        .body(StringBody("""{
          "lastName":"TestLastName",
          "birthdate":"1990-01-01",
          "email":"test@validation.com",
          "phone":"123456789",
          "street1":"123 Test St",
          "street2":"Apt 1",
          "city":"TestCity",
          "stateProvince":"TestState",
          "postalCode":"12345",
          "country":"TestCountry"
        }"""))
        .check(status.in(400, 422))
    )
    .exec(session => {
      println("Error recibido correctamente para falta de firstName")
      session
    })
    .pause(1)
    .exec(session => {
      println("Probando crear contacto con email duplicado...")
      session
    })
    .exec(
      http("Crear Contacto con Email Duplicado")
        .post("/contacts")
        .header("Authorization", "Bearer ${authToken}")
        .body(StringBody(s"""{
          "firstName":"Test",
          "lastName":"User",
          "birthdate":"1990-01-01",
          "email":"$email",
          "phone":"123456789",
          "street1":"123 Duplicate St",
          "street2":"Apt 2",
          "city":"DuplicateCity",
          "stateProvince":"DuplicateState",
          "postalCode":"54321",
          "country":"DuplicateCountry"
        }"""))
        .check(status.in(400, 409))
    )
    .exec(session => {
      println("Error recibido correctamente para email duplicado")
      session
    })

 

  setUp(
    // Historia 1: Login múltiple concurrente
    loginSuccessfulScenario.inject(
      rampUsersPerSec(2).to(10).during(5.seconds),
      constantUsersPerSec(5).during(5.seconds)
    ).protocols(httpConf),
    
    loginInvalidCredentialsScenario.inject(
      constantUsersPerSec(3).during(5.seconds)
    ).protocols(httpConf),
    
    loginMalformedEmailScenario.inject(
      constantUsersPerSec(2).during(5.seconds)
    ).protocols(httpConf),
    
    loginRequiredFieldsScenario.inject(
      constantUsersPerSec(2).during(5.seconds)
    ).protocols(httpConf),
    
    // Historia 2: Creación masiva de contactos
    createContactScenario.inject(
      rampUsersPerSec(1).to(8).during(5.seconds),
      constantUsersPerSec(8).during(5.seconds)
    ).protocols(httpConf),
    
    createContactValidationScenario.inject(
      constantUsersPerSec(2).during(5.seconds)
    ).protocols(httpConf)
  ).assertions(

    global.responseTime.max.lt(5000),
    global.responseTime.mean.lt(1000),
    global.successfulRequests.percent.gt(95),
    forAll.failedRequests.percent.lt(5)
  )
  
}