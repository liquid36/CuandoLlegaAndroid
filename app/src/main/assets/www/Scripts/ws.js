$(document).ready(function () {
  
var recDni = localStorage.getItem('keyDni');
var recTarjeta = localStorage.getItem('keyTarjeta');
document.getElementById("txtDni").value=recDni;
document.getElementById("txtNumTajeta").value=recTarjeta;

if(recDni != null){
	    document.getElementById("chkRecordar").checked=true;
		}
		else
		{ document.getElementById("chkRecordar").checked=false;
		}

document.form1.btnGetInfo.focus();
document.addEventListener("deviceready", onDeviceReady, false);
			  

function onDeviceReady()
{
	
document.addEventListener("backbutton", onBackClickEvent, false);
document.getElementById('oculto').click();
}

function onBackClickEvent()
{

if ( $('.ui-page-active').attr('id') == 'page1')
{
  if($('#table').is(':visible')) {
  window.location.reload();
}
  else
  {
  exitAppPopup();
  }
} 
else {
  history.back();             
     }
}
			
		
function exitAppPopup() {
navigator.notification.confirm(
'¿Esta seguro que desea salir?'
, function(button) {
if (button == 2) {
navigator.app.exitApp();
} 
}
, 'Salir', 'NO,SI');  
return false;
}
	
						
$("#btnGetInfo").click(function () {
        try {
			
	  if(document.getElementById('chkRecordar').checked)
			 {
			 var dni=document.getElementById("txtDni").value;
			 var tarjeta= document.getElementById("txtNumTajeta").value;
			 localStorage.setItem('keyDni', dni);
			 localStorage.setItem('keyTarjeta', tarjeta);
	         }
		    else
			 {
			 localStorage.removeItem('keyDni');
			 localStorage.removeItem('keyTarjeta')
			 }
			
			 $("#table").empty();
	    
			 var numeroDni = $("#txtDni").val();
			 var numeroTarjeta = $("#txtNumTajeta").val();
			
	        if (numeroDni == "" || numeroTarjeta=="" ) 
	        {
	
 			if (numeroDni == "" && numeroTarjeta=="" ) {
				navigator.notification.alert('Ingrese su documento y número de tarjeta', alertDismissed,'Mensaje', 'OK' );
			}
			else
		    {
			if (numeroDni == "") {
				navigator.notification.alert('Ingrese su documento', alertDismissed,'Mensaje', 'OK' );
			  }
			  
			  if (numeroTarjeta == "") {
				  navigator.notification.alert('Ingrese su número de tarjeta', alertDismissed,'Mensaje', 'OK' );
 			  }
		   }
	     }
	
	    else 
	  	
		if(numeroTarjeta.length!=17)
	  {
       navigator.notification.alert('Ingrese los 17 dígitos de su tarjeta', alertDismissed,'Mensaje', 'OK' );
	  }
	  
    else
	{

     	var ultDigito = parseInt(numeroTarjeta.substr(16, 1));
				
		 n1= parseInt(numeroTarjeta.substr(15, 1));
         n2= parseInt(numeroTarjeta.substr(14, 1));
         n3= parseInt(numeroTarjeta.substr(13, 1));
         n4= parseInt(numeroTarjeta.substr(12, 1));
         n5= parseInt(numeroTarjeta.substr(11, 1));
         n6= parseInt(numeroTarjeta.substr(10, 1));
         n7= parseInt(numeroTarjeta.substr(9, 1));
         n8= parseInt(numeroTarjeta.substr(8, 1));

         digitoVerificador= n1 ^ n2^ n3^ n4^ n5^ n6^ n7^ n8
		 
		var digitoVerificador1 = digitoVerificador.toString();
		var digitoVerificador2 = digitoVerificador1.substr(digitoVerificador1.length - 1);
		var digitoVerificador3 = parseInt(digitoVerificador2);
		
		if(ultDigito!=digitoVerificador3)
	    {
	    navigator.notification.alert('El número de tarjeta ingresado es inválido', alertDismissed,'Mensaje', 'OK' );
		}

	    else
		{
            $("#table").empty();
            MostrarMsjePeticionEnviada(true);
            ObtenerOperaciones();
		}}}
		
        catch (ex) {
            MostrarMsjePeticionEnviada(false);
            navigator.notification.alert('No se pudo conectar con el servidor. Verifique su conexión', alertDismissed,'Error', 'OK' );
        }
    });
	

});


function alertDismissed() {
}


function ObtenerOperaciones() {

    var numeroTarjeta = $("#txtNumTajeta").val().substr(0, 16);
    var dniUsuario = $("#txtDni").val();
    var webMethod = "http://190.216.78.10/operacionestsc/ServiceTSC.asmx/ObtenerOperaciones";       
    var valor = numeroTarjeta + "," + dniUsuario;    
    var encrypted = EncryptarAes(valor);
    encrypted = encrypted.toString();      
    var parameters = { 'dataUser': encrypted };
    
    $.ajax({
        type: "POST",
        url: webMethod,
        data: JSON.stringify(parameters),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
		timeout:55000,
        async:true,
        success: function (msg) {
         
            $("#table").addClass("hidden");
            $('#WSContent').empty();
			$('#table').append('class="hidden"> <tr> <th>Fecha</th> <th>Linea</th><th>Tipo</th><th>Importe</th><th>Saldo</th></tr>');

            try {

                var resultado = $.parseJSON(msg.d);
                if (resultado.estado == 0) {
			    MostrarGrilla(resultado.movimientos);
                }

                else {
					
			      if (resultado.estado == 1) {
			      navigator.notification.alert(resultado.mensaje,alertDismissed,'Mensaje', 'OK');
                  }
				
				  if (resultado.estado == 2) {
			      navigator.notification.alert(resultado.mensaje,alertDismissed,'Saldo', 'OK');
				  navigator.notification.alert('En este momento no es posible mostrar el detalle de los movimientos, solo se mostrará el último saldo de la tarjeta.',alertDismissed,'Mensaje','OK');
                  }
				
				  if (resultado.estado == 3) {
			
                  navigator.notification.alert(resultado.mensaje,alertDismissed,'Saldo', 'OK');
 				  navigator.notification.alert('No se realizaron operaciones en los últimos días, por lo que sólo se mostrará el último saldo de la tarjeta, sin el detalle de los movimientos.',alertDismissed,'Mensaje','OK');
                  }
				
				  if (resultado.estado == 4) {
		          navigator.notification.alert(resultado.mensaje,alertDismissed,'Mensaje', 'OK');
                  }
				  
				  if (resultado.estado == 5) {
			      navigator.notification.alert(resultado.mensaje,alertDismissed,'Mensaje', 'OK');
                  }
				
		       }
            }
            catch (e)
            {
                alert(e)
            }

            MostrarMsjePeticionEnviada(false);
        },
            error: function (data, status, error) {
            MostrarMsjePeticionEnviada(false);
            navigator.notification.alert('No se pudo conectar con el servidor', alertDismissed,'Error', 'OK' );
        }
    });
}



function Encryptar(valor) {

    var EncryptionKey = "0p-rac1on3sT$C.G0dz1ll@";

    var encrypted = "";

    for (var i = 0; i < valor.length; i++)
    {
        var caracter = valor[i];
        var num = GetNum(caracter);

        var symbolChar = EncryptionKey[num];

        encrypted += GetAsciiValue(symbolChar);
    }

    return encrypted;

}

function GetNum(value)
{
    switch (value)
    {
        case ",":
                return 14;                    
        default:            
            return parseInt(value);
    }
}

function GetAsciiValue(symbol)
{
    var asciiCode = symbol.charCodeAt(0);
    var entero =  parseInt(asciiCode);

    if(entero < 10) return "00" + asciiCode;
    else if(entero < 100) return "0" + asciiCode;

    return asciiCode;
}

function ObtenerSaldo() {


    var numeroTarjeta = $("#txtNumTajeta").val();    
    var webMethod = "http://190.216.78.10/operacionestsc/ServiceTSC.asmx/ObtenerUltimoSaldo";
    var parameters = { 'numTarjeta': numeroTarjeta };

    $.ajax({
        type: "POST",
        url: webMethod,
        data: JSON.stringify(parameters),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        async: true,
        success: function (msg) {
       
        $('#WSContent').html(msg.d);
        },
        error: function (data, status, error) {
             navigator.notification.alert('No se pudo conectar con el servidor', alertDismissed,'Error', 'OK' );
        }
    });
}


function MostrarMsjePeticionEnviada(inicia)
{
  
	 if (inicia){
		 $("#btnGetInfo").prop( "disabled", true );
		 navigator.notification.activityStart("Consultando", "Espere un momento por favor");
	 }
	 
	  else {
		  $("#btnGetInfo").prop( "disabled", false );
		  navigator.notification.activityStop();  
	 
	
	  }
	  
	  
}


function EncryptarAes(valor)
{
    var EncryptionKey = "0p-rac1on3sT$C.G0dz1ll@";
    var key = CryptoJS.enc.Utf8.parse('AMINHAKEYTEM32NYTES1234567891234');
    var iv = CryptoJS.enc.Utf8.parse('7061737323313233');
    var encrypted = CryptoJS.AES.encrypt(CryptoJS.enc.Utf8.parse(valor), EncryptionKey, key,
    { 
         keySize: 128,
         iv: iv, 
         mode: CryptoJS.mode.CBC,
         padding: CryptoJS.pad.Pkcs7 
    }); 
    return encrypted;
}
