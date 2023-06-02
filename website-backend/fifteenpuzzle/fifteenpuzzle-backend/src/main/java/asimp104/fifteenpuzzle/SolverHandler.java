package asimp104.fifteenpuzzle;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class SolverHandler {
	
	@GetMapping("/greeting")
	public String greeting () {
		return "Hello";
	}
}
