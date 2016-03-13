function MostrarGrilla(data)
{
    if (data) {
        var len = data.length;
        
        var txt = "";
        if (len > 0) {
            for (var i = 0; i < len; i++) {                
                txt += "<tr>" +
                       "<td align='center'>" + data[i].fecha + "</td>" +
                       "<td align='center'>" + data[i].linea + "</td>" +
                       "<td align='left'>" + data[i].tipo + "</td>" +
                       "<td align='center'>" + data[i].importe + "</td>" +
                       "<td align='center'>" + data[i].saldo + "</td>" +
                       "</tr>";
            }
            if (txt != "") {
				
             var b = document.getElementById("txtNumTajeta").value;
	         $("#table").append(txt).removeClass("hidden");
             document.getElementById('prueba').style.display = 'none';
		     document.getElementById('divBtn').style.display = 'none';
			 window.scrollTo(0,0);
		     $("#nrotarjeta").removeClass("hidden");
		     $("#volver").removeClass("hidden");
			 $("#nrotarjeta").append("&nbsp;Nro. de tarjeta:&nbsp;"+b);
			 $("#ultimosaldo").removeClass("hidden");
			 
					   var fechaUltimoViaje = $('#table tr:eq(1) td:eq(0)').text()
					   var saldoUltimoViaje = $('#table tr:eq(1) td:eq(4)').text()	
					   var tipoUltimoViaje = $('#table tr:eq(1) td:eq(2)').text()
					   
					   if(tipoUltimoViaje=="Plus 1")
					   {
						 $("#ultimosaldo").append("&nbsp;Saldo al "+fechaUltimoViaje+" hs : $ "+saldoUltimoViaje+" (Debe 1 viaje Plus)");
					   }
					   
					   if(tipoUltimoViaje=="Plus 2")
					   {
						 $("#ultimosaldo").append("&nbsp;Saldo al "+fechaUltimoViaje+" hs : $ "+saldoUltimoViaje+" (Debe 2 viajes Plus)");
					   }
					   
					   if(tipoUltimoViaje!="Plus 1" &&tipoUltimoViaje!="Plus 2")
					   {
					    $("#ultimosaldo").append("&nbsp;Saldo a la fecha "+fechaUltimoViaje+" hs : $ "+saldoUltimoViaje);
					  }

    	        }
        }
    }
   
}