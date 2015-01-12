/*
 * Copyright (C) 2015 David Pérez Cabrera <dperezcabrera@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.poker.api.core.cc;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.poker.api.core.CardUtil4Testing;
import org.poker.api.core.HandEvaluator;
import org.poker.api.core.IHandEvaluator;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class HandEvaluatorSteps {
    private static final String[] VALORES = {"mano0", "iguales", "mano1"};
    private IHandEvaluator handEvaluator;
    private String resultado;
    
    
    @Given("^un HandEvaluator$")
    public void un_HandEvaluator() throws Throwable {
        handEvaluator = new HandEvaluator();
    }
    
    @When("^calculamos la comparacion entre (.*) y (.*)$")
    public void calculamos_la_comparacion_entre(String hand0, String hand1) {
        int evalhand0 = handEvaluator.eval(CardUtil4Testing.fromStringCards(hand0));
        int evalhand1 = handEvaluator.eval(CardUtil4Testing.fromStringCards(hand1));
        int diferencia = evalhand1 - evalhand0;
        if (diferencia != 0) {
            diferencia = Math.abs(diferencia) / diferencia;
        }
        resultado = VALORES[diferencia + 1];
    }
    
    @Then("^el resultado esperado es (.*)$")
    public void el_resultado_esperado_es(String resultadoEsperado) throws Throwable {
        assertEquals(resultadoEsperado, resultado);
    }
}
