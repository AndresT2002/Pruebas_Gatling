package Demo

object Data{
    val url = "https://thinking-tester-contact-list.herokuapp.com"
    // Credenciales reales del usuario
    val email = "andresprueba@hotmail.com"
    val password = "1234567"
    
    // Credenciales inválidas para pruebas de error
    val invalidEmail = "testttt@testtt.com"
    val invalidPassword = "1111"
    val malformedEmail = "andrespruebahotmail.com"
    
    // Datos para creación de contactos
    val contactFirstNames = List("Juan", "María", "Carlos", "Ana", "Pedro", "Laura", "Miguel", "Carmen", "Roberto", "Elena")
    val contactLastNames = List("García", "Rodríguez", "González", "Fernández", "López", "Martínez", "Sánchez", "Pérez", "Martín", "Gómez")
    val contactEmails = List(
        "juan.garcia@test.com", 
        "maria.rodriguez@test.com", 
        "carlos.gonzalez@test.com",
        "ana.fernandez@test.com",
        "pedro.lopez@test.com",
        "laura.martinez@test.com",
        "miguel.sanchez@test.com",
        "carmen.perez@test.com",
        "roberto.martin@test.com",
        "elena.gomez@test.com"
    )
    val contactPhones = List("123456789", "987654321", "555123456", "666789123", "777456789", "888123456", "999654321", "111222333", "444555666", "777888999")
    val contactBirthdates = List("1990-01-15", "1985-06-22", "1992-11-08", "1988-03-14", "1995-09-27", "1987-12-05", "1993-04-18", "1991-07-30", "1989-10-12", "1994-02-25")
    
    // Nuevos datos para direcciones completas
    val contactStreet1 = List(
        "123 Main St", "456 Oak Ave", "789 Pine Rd", "321 Elm St", "654 Maple Dr",
        "987 Cedar Ln", "147 Birch Ct", "258 Willow Way", "369 Spruce Blvd", "741 Aspen Pl"
    )
    val contactStreet2 = List(
        "Apt 1A", "Suite 200", "Unit B", "Floor 3", "Apt 2B", 
        "Suite 150", "Unit C", "Apt 4A", "Suite 300", "Unit D"
    )
    val contactCities = List(
        "Madrid", "Barcelona", "Valencia", "Sevilla", "Bilbao",
        "Málaga", "Zaragoza", "Murcia", "Palma", "Las Palmas"
    )
    val contactStatesProvinces = List(
        "Madrid", "Cataluña", "Valencia", "Andalucía", "País Vasco",
        "Castilla y León", "Galicia", "Castilla-La Mancha", "Canarias", "Murcia"
    )
    val contactPostalCodes = List(
        "28001", "08001", "46001", "41001", "48001",
        "29001", "50001", "30001", "07001", "35001"
    )
    val contactCountries = List(
        "España", "Spain", "ES", "Mexico", "Colombia", 
        "Argentina", "Chile", "Perú", "Venezuela", "Ecuador"
    )
}
