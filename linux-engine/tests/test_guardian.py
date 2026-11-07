import contextlib
import io
import json
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch

import guardian


class GuardianEngineTests(unittest.TestCase):
    def test_simulation_emits_requested_cycles_and_summary(self):
        with tempfile.TemporaryDirectory() as tmp:
            with patch.object(guardian, "LOG_DIR", Path(tmp)):
                output = io.StringIO()
                with contextlib.redirect_stdout(output):
                    code = guardian.simulate(cycles=3, recovery_ms=900)

            self.assertEqual(code, 0)
            events = [json.loads(line) for line in
                      (Path(tmp) / f"guardian-{guardian.datetime.now().strftime('%Y%m%d')}.jsonl")
                      ).read_text(encoding="utf-8").splitlines()]
            cycle_events = [event for event in events
                            if event.get("type") == "resilience_simulation"]
            self.assertEqual(len(cycle_events), 3)
            self.assertEqual([event["cycle"] for event in cycle_events], [1, 2, 3])
            self.assertTrue(all(event["mode"] == "local_simulation_only"
                                for event in cycle_events))
            self.assertEqual(events[-1]["type"], "simulation_summary")
            self.assertIn("SIMULATION ONLY", output.getvalue())

    def test_cli_rejects_scan_duration_out_of_range(self):
        with patch("sys.argv", ["guardian.py", "scan", "--seconds", "61"]):
            with self.assertRaises(SystemExit) as raised:
                guardian.main()
            self.assertEqual(raised.exception.code, 2)

    def test_cli_rejects_simulation_cycles_out_of_range(self):
        with patch("sys.argv", ["guardian.py", "simulate-resilience", "--cycles", "0"]):
            with self.assertRaises(SystemExit) as raised:
                guardian.main()
            self.assertEqual(raised.exception.code, 2)


if __name__ == "__main__":
    unittest.main()
