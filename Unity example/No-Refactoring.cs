using System.Collections;
using System.Collections.Generic;
using Cinemachine;
using UnityEngine;

public class CameraHandler : MonoBehaviour {
    [SerializeField]
    private CinemachineVirtualCamera cam;

    private float a;
    private float b;

    private void Start () {
        a = cam.m_Lens.OrthographicSize;
        b = a;
    }
    private void Update () {
        float x = Input.GetAxisRaw ("Horizontal");
        float y = Input.GetAxisRaw ("Vertical");

        Vector3 m = new Vector3 (x, y, 0).normalized;
        float s = 30f;
        transform.position += m * s * Time.deltaTime;

        float z = 2f;
        b -= Input.mouseScrollDelta.y * z;
        float min = 10;
        float max = 30;

        b = Mathf.Clamp (b, min, max);
        float zs = 5f;
        a = Mathf.Lerp (a, b, Time.deltaTime * zs);

        cam.m_Lens.OrthographicSize = a;
    }
}